package org.jfox.mvc.invocation;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionInvocationHandler;
import org.jfox.mvc.FileUploaded;
import org.jfox.mvc.InvocationException;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.ParameterObject;
import org.jfox.mvc.servlet.ControllerServlet;
import org.jfox.mvc.validate.ValidateException;
import org.jfox.mvc.validate.Validators;
import org.jfox.util.ClassUtils;

import javax.servlet.ServletException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 1:19:39 PM
 */
public class ParseParameterActionInvocationHandler extends ActionInvocationHandler{

    /**
     * 保存 invocationClass 到其 Filed/Annotation的映射
     */
    private Map<Class<? extends ParameterObject>, Map<String, FieldValidation>> invocationMap = new HashMap<Class<? extends ParameterObject>, Map<String, FieldValidation>>();

    public PageContext invoke(ActionContext actionContext, Iterator<ActionInvocationHandler> chain) throws Exception {
        // 会导致取出的值为数组问题，所以只能使用下面的循环
        final Map<String,String[]> parameterMap = new HashMap<String, String[]>();
        final Map<String, FileUploaded> fileUploadedMap = new HashMap<String, FileUploaded>();

        //设置跳转方式
        actionContext.getPageContext().setForwardMethod(actionContext.getActionMethodAnnotation().forwardMethod());

        // invocation class
        Class<? extends ParameterObject> invocationClass = actionContext.getParameterClass();

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
                catch (FileUploadException e) {
                    throw new ServletException("File upload failed!", e);
                }
            }
        }

        actionContext.setParameterMap(parameterMap);
        actionContext.setFileUploadedMap(fileUploadedMap);

        try {
            ParameterObject parameterObject = initInvocation(invocationClass, actionContext);
            actionContext.setInvocation(parameterObject);
            return super.next(actionContext, chain);
        }
        catch (InvocationException e) {
            //invocation exception, throw out
            throw e; // throw out InvocationException
        }
        catch (ValidateException e) {
            //invocation validate exception
            actionContext.getPageContext().setTargetView(actionContext.getErrorView());
            actionContext.getPageContext().addValidateException(e);
            // 设置 J_VALIDATE_EXCEPTIONS 变量
            actionContext.getPageContext().setAttribute("J_VALIDATE_EXCEPTIONS", actionContext.getPageContext().getValidateExceptions());
            // 直接返回，不再执行下面的操作
            return actionContext.getPageContext();
        }
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


    /**
     * build Invocation
     * validator input
     * set invocation field value
     *
     * @param invocationClass   incation class
     * @param actionContext invcation context
     * @throws org.jfox.mvc.InvocationException throw when contruct invocation failed
     * @throws org.jfox.mvc.validate.ValidateException   throw when invocation attribute validate failed
     */
    protected ParameterObject initInvocation(Class<? extends ParameterObject> invocationClass, ActionContext actionContext) throws InvocationException, ValidateException {
        ParameterObject parameterObject;
        if (invocationClass.equals(ParameterObject.class)) {
            parameterObject = new ParameterObject() {
            };
            parameterObject.init(Collections.EMPTY_MAP, actionContext.getParameterMap(), actionContext.getFilesUploaded());
        }
        else {
            try {
                parameterObject = invocationClass.newInstance();
                // verify input then build fields
            }
            catch (Exception e) {
                throw new InvocationException("Construct invocation exception.", e);
            }
        }
        parameterObject.init(getInvocationFieldValidationMap(invocationClass), actionContext.getParameterMap(), actionContext.getFilesUploaded());
        /**
         * 有些需要关联验证的，比如：校验两次输入的密码是否正确，
         * 因为不能保证校验密码在初试密码之后得到校验，所以必须放到 validateAll 中进行校验
         */
        parameterObject.validateAll();
        return parameterObject;
    }

    private Map<String, FieldValidation> getInvocationFieldValidationMap(Class<? extends ParameterObject> invocationClass) {
        if (invocationMap.containsKey(invocationClass)) {
            return Collections.unmodifiableMap(invocationMap.get(invocationClass));
        }
        //构造 fieldMap & validationMap
        Field[] allFields = ClassUtils.getAllDecaredFields(invocationClass);
        Map<String, FieldValidation> fieldValidationMap = new HashMap<String, FieldValidation>(allFields.length);

        for (Field field : allFields) {
            if (!field.getDeclaringClass().equals(ParameterObject.class)) { //过滤掉Invocation自身的Field
                if (fieldValidationMap.containsKey(field.getName())) {
                    logger.warn("Reduplicate filed name: " + field.getName() + " in invocation: " + invocationClass.getName());
                    continue;
                }
                Annotation validationAnnotation = getAvailableValidationAnnotation(field);
                FieldValidation fieldValidation = new FieldValidation(field, validationAnnotation);
                fieldValidationMap.put(field.getName(), fieldValidation);
            }
        }
        invocationMap.put(invocationClass, fieldValidationMap);
        return Collections.unmodifiableMap(fieldValidationMap);
    }

    private Annotation getAvailableValidationAnnotation(Field field) {
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

    public static void main(String[] args) {

    }
}