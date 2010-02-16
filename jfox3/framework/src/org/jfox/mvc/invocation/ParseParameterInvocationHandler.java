package code.google.webactioncontainer.invocation;

import code.google.jcontainer.invoke.Invocation;
import code.google.jcontainer.invoke.InvocationHandler;
import code.google.jcontainer.util.ClassUtils;
import code.google.webactioncontainer.ActionException;
import code.google.webactioncontainer.ActionRuntimeException;
import code.google.webactioncontainer.validate.ValidateResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import code.google.webactioncontainer.ActionContext;
import code.google.webactioncontainer.FileUploaded;
import code.google.webactioncontainer.InvocationException;
import code.google.webactioncontainer.ParameterObject;
import code.google.webactioncontainer.servlet.ControllerServlet;
import code.google.webactioncontainer.validate.ValidateException;
import code.google.webactioncontainer.validate.Validators;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 1:19:39 PM
 */
public class ParseParameterInvocationHandler implements InvocationHandler {

    private static Log logger = LogFactory.getLog(ParseParameterInvocationHandler.class);

    /**
     * 保存 invocationClass 到其 Filed/Annotation的映射
     */
    private static Map<Class<? extends ParameterObject>, Map<String, FieldValidation>> parameterClassMap = new HashMap<Class<? extends ParameterObject>, Map<String, FieldValidation>>();

    public void chainInvoke(Invocation invocation) throws Exception {
        ActionContext actionContext = (ActionContext)invocation.getParameters()[0];
        Class<? extends ParameterObject> invocationClass = actionContext.getParameterClass();

        // parameter class
        initParameterMap(actionContext);

        ParameterObject parameterObject = createParameterObject(invocationClass, actionContext);
        actionContext.setParameterObject(parameterObject);

    }

    public void chainReturn(Invocation invocation) throws Exception {

    }

    public void onCaughtException(Invocation invocation, Exception e) {
        invocation.onCaughtException(e);
    }

    private String[] decodeQueryStringParameterValues(String[] values) {
        if(values != null && values.length > 0) {
            String[] decodedValues = new String[values.length];
            for(int i=0; i<values.length; i++) {
                try {
                    decodedValues[i] = new String(values[i].getBytes("ISO-8859-1"), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    decodedValues[i] = values[i];
                }
            }
            return decodedValues;
        }
        else {
            return new String[0];
        }
    }


    private void initParameterMap(ActionContext actionContext) throws ActionException {
        // 会导致取出的值为数组问题，所以只能使用下面的循环
        final Map<String,String[]> parameterMap = new HashMap<String, String[]>();
        final Map<String, FileUploaded> fileUploadedMap = new HashMap<String, FileUploaded>();

        //设置跳转方式
        actionContext.getPageContext().setForwardMethod(actionContext.getActionMethodAnnotation().forwardMethod());


        String queryString = actionContext.getServletRequest().getQueryString();
        if (!actionContext.isMultipartContent()) {
            for (Enumeration enu = actionContext.getServletRequest().getParameterNames(); enu.hasMoreElements();) {
                String key = (String)enu.nextElement();
                String[] values = actionContext.getServletRequest().getParameterValues(key);
//                invocationContext.addParameter(key, values);
                if(queryString != null && queryString.contains(key)) { // 是 URL encoding
                    values = decodeQueryStringParameterValues(values);
                }
                parameterMap.put(key,values);
            }
        }
        else { // 有文件上传
            //  从 HTTP servlet 获取 fileupload 组件需要的内容
            RequestContext requestContext = new ServletRequestContext(actionContext.getServletRequest());
            //  判断是否包含 multipart 内容
            if (ServletFileUpload.isMultipartContent(requestContext)) {
                //  创建基于磁盘的文件工厂
                DiskFileItemFactory factory = new DiskFileItemFactory();
                //  设置直接存储文件的极限大小，一旦超过则写入临时文件以节约内存。默认为 1024 字节
                factory.setSizeThreshold(ControllerServlet.MAX_UPLOAD_FILE_SIZE);
                //  创建上传处理器，可以处理从单个 HTML 上传的多个上传文件。
                ServletFileUpload upload = new ServletFileUpload(factory);
                //  最大允许上传的文件大小 5M
                upload.setSizeMax(ControllerServlet.MAX_UPLOAD_FILE_SIZE);
                upload.setHeaderEncoding(ControllerServlet.DEFAULT_ENCODING);
                try {
                    //  处理上传
                    List items = upload.parseRequest(requestContext);
                    //  由于提交了表单字段信息，需要进行循环区分。
                    for (Object item : items) {
                        FileItem fileItem = (FileItem)item;
                        if (fileItem.isFormField()) {
                            // 表单内容
//                            invocationContext.addParameter(fileItem.getFieldName(), new String[]{fileItem.getString(DEFAULT_ENCODING)});
                            parameterMap.put(fileItem.getFieldName(), new String[]{fileItem.getString(ControllerServlet.DEFAULT_ENCODING)});
                        }
                        else {
                            //  如果不是表单内容，取出 multipart。
                            //  上传文件路径和文件、扩展名。
                            String sourcePath = fileItem.getName();
                            //  获取真实文件名
                            String fileName = new File(sourcePath).getName();
                            // 读到内存成 FileUpload 对象
                            FileUploaded fileUploaded = new FileUploaded(fileItem.getFieldName(), fileName, fileItem.get());
//                            invocationContext.addFileUploaded(fileItem.getFieldName(), fileUploaded);
                            fileUploadedMap.put(fileItem.getFieldName(), fileUploaded);
                        }
                    }
                }
                catch (Exception e) {
                    throw new ActionException("File upload failed!", e);
                }
            }
        }

        actionContext.setParameterMap(parameterMap);
        actionContext.setFileUploadedMap(fileUploadedMap);
    }

    /**
     * build Invocation
     * validator input
     * set invocation field value
     *
     * @param parameterObjectClass   incation class
     * @param actionContext invcation context
     * @throws code.google.webactioncontainer.InvocationException throw when contruct invocation failed
     * @throws code.google.webactioncontainer.validate.ValidateException   throw when invocation attribute validate failed
     */
    protected static ParameterObject createParameterObject(Class<? extends ParameterObject> parameterObjectClass, ActionContext actionContext) throws ActionException {
        ParameterObject parameterObject;
        if (parameterObjectClass.equals(ParameterObject.class)) {
            parameterObject = new ParameterObject() {
            };
            initParameterObject(parameterObject, Collections.EMPTY_MAP, actionContext.getParameterMap(), actionContext.getFilesUploaded());
        }
        else {
            try {
                parameterObject = parameterObjectClass.newInstance();
                // verify input then build fields
            }
            catch (Exception e) {
                throw new RuntimeException("Construct invocation exception.", e);
            }
        }
        initParameterObject(parameterObject, getParameterClassFieldValidationMap(parameterObjectClass), actionContext.getParameterMap(), actionContext.getFilesUploaded());
        /**
         * 有些需要关联验证的，比如：校验两次输入的密码是否正确，
         * 因为不能保证校验密码在初试密码之后得到校验，所以必须放到 validateAll 中进行校验
         */

        try {
            parameterObject.validateAll();
        }
        catch (ValidateException ve) {
            ValidateResult validateResult = new ValidateResult(ve.getInputField(), ve.getInputValue(), null);
            validateResult.setSuccess(false);
            validateResult.setErrorMessage(Validators.getErrorMessage(ve.getInputField(), ve.getInputValue(),ve.getErrorId()));
            parameterObject.addErrorValidates(validateResult);
        }
        return parameterObject;
    }

    /**
     * 设置所有数据，并进行校验
     * @param fieldValidationMap fieldValidationMap
     * @param parameterMap 提交的 http request parameterMap
     * @param fileUploadeds 上传的文件
     * @throws ValidateException valiate
     * @throws InvocationException invocation
     */
    protected static void initParameterObject(ParameterObject parameterObject, Map<String, FieldValidation> fieldValidationMap, Map<String, String[]> parameterMap, Collection<FileUploaded> fileUploadeds) throws ActionException{
        parameterObject.addAttributes(parameterMap);
        // verify & build form field from parameterMap
        // 复制一份
        fieldValidationMap = new HashMap<String, FieldValidation>(fieldValidationMap);
        // loop parameterMap
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            try {
                FieldValidation fieldValidation = fieldValidationMap.remove(key);
                if(fieldValidation == null) {
                    // if a servlet Parameter don't have a corresponding FieldValidation
                    // every field should has FieldValidation, although the ValidationAnnotation may be null                    
                    String msg = "Set request parameter to " + parameterObject.getClass().getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed, No such filed!";
                    //just a log message
                    logger.warn(msg);
                    continue;
                }
                Field field = fieldValidation.getField();
                field.setAccessible(true);
                Annotation validationAnnotation = fieldValidation.getValidationAnnotation();

                Class<?> fieldType = field.getType();
                if(fieldType.equals(FileUploaded.class)) {
                    logger.warn("ignore FileUploaded field: " + field);
                }
                else if (fieldType.isArray()) {
                    Class<?> arrayType = fieldType.getComponentType();
                    Object paramArray = Array.newInstance(arrayType, values.length);
                    for (int i = 0; i < Array.getLength(paramArray); i++) {
                        if (validationAnnotation != null) {
                            ValidateResult validateResult = Validators.validate(field, values[i], validationAnnotation);
                            if(!validateResult.isSuccess()) {
                                parameterObject.addErrorValidates(validateResult);
                            }
                            else {
                                Array.set(paramArray,i, validateResult.getReturnObject());
                            }
                        }
                        else {
                            //no validator, try to use ClassUtils construct object
                            Array.set(paramArray,i, ClassUtils.newObject(arrayType, values[i]));
                        }
                    }
                    field.set(parameterObject, paramArray);
                }
                else {
                    String value = values[0];
                    Object v = null;
                    if (validationAnnotation != null) {
                        ValidateResult validateResult = Validators.validate(field, value, validationAnnotation);
                        if(!validateResult.isSuccess()) {
                            parameterObject.addErrorValidates(validateResult);
                        }
                        else {
                            field.set(parameterObject, validateResult.getReturnObject());
                        }
                    }
                    else {
                        v = ClassUtils.newObject(fieldType, value);
                        field.set(parameterObject, v);
                    }
                }
            }
            catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                String msg = "Set request parameter to + " + parameterObject.getClass().getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed!";
                logger.error(msg, t);
                throw new ActionException(msg, t);
            }
            catch (Throwable t) {
                String msg = "Set request parameter to + " + parameterObject.getClass().getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed!";
                logger.error(msg, t);
                throw new ActionException(msg, t);
            }
        }

        for (FileUploaded fileUploaded : fileUploadeds) {
            String fieldName = fileUploaded.getFieldname();

            FieldValidation fieldValidation = fieldValidationMap.remove(fieldName);
            Annotation validationAnnotation = fieldValidation.getValidationAnnotation();

            try {
                Field field = ClassUtils.getDeclaredField(parameterObject.getClass(), fieldName);
                if (validationAnnotation != null) {
                    ValidateResult validateResult = Validators.validate(field, fileUploaded.getFilename(), validationAnnotation);
                    if(!validateResult.isSuccess()) {
                        parameterObject.addErrorValidates(validateResult);
                    }
                }
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (FileUploaded.class.isAssignableFrom(fieldType)) {
                    field.set(parameterObject, fileUploaded);
                }
                else {
                    String msg = "Invocation " + parameterObject.getClass().getName() + " 's field " + field.getName() + " is not a type " + FileUploaded.class.getName();
                    logger.warn(msg);
                    throw new ActionException(msg);
                }
            }
            catch(ActionException e) {
                throw e;
            }
            catch (Exception e) {
                String msg = "Set request parameter to " + parameterObject.getClass().getName() + "'s FileUploaded field " + fieldName + " with value " + fileUploaded + " failed!";
                logger.warn(msg, e);
                throw new ActionException(msg, e);
            }
        }

        // 检查是否有必须的field还没有设置
        for(FieldValidation fieldValidation : fieldValidationMap.values()){
            Annotation validationAnnotation = fieldValidation.getValidationAnnotation();
            if(validationAnnotation != null) {
                ValidateResult validateResult = Validators.validateNullable(parameterObject, fieldValidation.getField(), validationAnnotation);
                if(!validateResult.isSuccess()) {
                    parameterObject.addErrorValidates(validateResult);
                }
            }
        }
    }

    private static Map<String, FieldValidation> getParameterClassFieldValidationMap(Class<? extends ParameterObject> parameterClass) {
        if (parameterClassMap.containsKey(parameterClass)) {
            return Collections.unmodifiableMap(parameterClassMap.get(parameterClass));
        }
        //构造 fieldMap & validationMap
        Field[] allFields = ClassUtils.getAllDeclaredFields(parameterClass);
        Map<String, FieldValidation> fieldValidationMap = new HashMap<String, FieldValidation>(allFields.length);

        for (Field field : allFields) {
            if (!field.getDeclaringClass().equals(ParameterObject.class)) { //过滤掉ParameterObject超类自身的Field
                if (fieldValidationMap.containsKey(field.getName())) {
                    logger.warn("Reduplicate filed name: " + field.getName() + " in invocation: " + parameterClass.getName());
                    continue;
                }
                Annotation validationAnnotation = getValidationAnnotation(field);
                FieldValidation fieldValidation = new FieldValidation(field, validationAnnotation);
                fieldValidationMap.put(field.getName(), fieldValidation);
            }
        }
        parameterClassMap.put(parameterClass, fieldValidationMap);
        return Collections.unmodifiableMap(fieldValidationMap);
    }

    private static Annotation getValidationAnnotation(Field field) {
        int count = 0;
        Annotation validAnnotation = null;
        Annotation[] fieldAnnotations = field.getAnnotations();
        for (Annotation annotation : fieldAnnotations) {
            if (Validators.isValidationAnnotation(annotation)) {
                validAnnotation = annotation;
                count++;
            }
        }
        if (count == 0) {
            return null;
        }
        else if (count > 1) {
            logger.warn("More than one Validation Annotation on " + field + ", will use last one.");
            return validAnnotation;
        }
        else {
            return validAnnotation;
        }
    }

    public static class FieldValidation {
        private Field field;
        private Annotation validationAnnotation;

        public FieldValidation(Field field, Annotation validationAnnotation) {
            this.field = field;
            this.validationAnnotation = validationAnnotation;
        }

        public Field getField() {
            return field;
        }

        public Annotation getValidationAnnotation() {
            return validationAnnotation;
        }
    }
}