/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.jfox.mvc.validate.ValidateException;
import org.jfox.mvc.validate.Validators;
import org.jfox.util.ClassUtils;

/**
 * MVC Invocation
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Invocation {

    /**
     * 存的原始数据，从 HttpRequest.parameterMap 复制过来，比如：对于 upload field，这里存的只是 filedname
     * value 统一用数组保存，这样可以处理 checkbox
     */
    private Map<String, String[]> attributes = new HashMap<String, String[]>();

    /**
     * 缓存该Invocation的校验
     * field name => field annotation
     */
    private Map<String, Annotation> validationMap = new ConcurrentHashMap<String, Annotation>();

    private Map<String, Field> fieldMap = new ConcurrentHashMap<String, Field>();

    /**
     * TODO: 保存 invocationClass 到其 Filed/Annotation的映射
     */
    private Map<Class<? extends Invocation>, String> invocationMap = new WeakHashMap<Class<? extends Invocation>, String>();

    /**
     * form 中需要有 <input type="hidden" name="request_token" value="$J_REQUESET_TOKEN"> 
     */
    private String request_token = null;

    private Logger logger = Logger.getLogger(this.getClass());

    public Invocation() {
        
    }

    /**
     * 设置所有数据，并进行校验
     * @param parameterMap 提交的 http request parameterMap
     * @param fileUploadeds 上传的文件
     * @throws ValidateException valiate
     * @throws InvocationException invocation
     */
    final void putAll(Map<String, String[]> parameterMap, Collection<FileUploaded> fileUploadeds) throws ValidateException, InvocationException {
        initValidationMap();
        
        attributes.putAll(parameterMap);
        // verify & build form field from parameterMap
        ValidateException validateException = null;

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            try {
                Field field = fieldMap.get(key);
                if(field == null) {
                    //仅仅发出一个信息
                    String msg = "Set invocation " + this.getClass().getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed, No such filed!";
                    logger.info(msg);
                    continue;
                }
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                if (fieldType.isArray()) {
                    Class<?> arrayType = fieldType.getComponentType();
                    Object[] params = (Object[])Array.newInstance(arrayType, values.length);
                    for (int i = 0; i < params.length; i++) {
                        if (validationMap.containsKey(key)) {
                            try {
                                // valiate field input and construct
                                params[i] = Validators.validate(field, values[i], validationMap.get(key));
                            }
                            catch (ValidateException e) {
                                // 只记录第一个 ValidateException
                                if (validateException == null) {
                                    validateException = e;
                                }
                            }

                        }
                        else {
                            //no validator, try to use ClassUtils construct object
                            params[i] = ClassUtils.newObject(arrayType, values[i]);
                        }
                    }
                    field.set(this, params);
                }
                else {
                    String value = values[0];
                    Object v = null;

                    if (validationMap.containsKey(key)) {
                        try {
                            v = Validators.validate(field, value, validationMap.get(key));
                        }
                        catch (ValidateException e) {
                            // 只记录第一个 ValidateException
                            if (validateException == null) {
                                validateException = e;
                            }
                        }
                    }
                    else {
                        v = ClassUtils.newObject(fieldType, value);
                    }
                    field.set(this, v);
                }
            }
            catch (Throwable t) {
                String msg = "Set invocation + " + this.getClass().getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed!";
                logger.warn(msg, t);
                throw new InvocationException(msg, t);
            }
        }

        if (validateException != null) {
            String msg = "Set invocation + " + this.getClass().getName() + "'s field \"" + validateException.getInputField() + "\" with value \"" + validateException.getInputValue() + "\" failed, " + validateException.getMessage();
            logger.warn(msg);
            throw validateException; // throw exception to execute()
        }

                // build upload file field
        for (FileUploaded fileUploaded : fileUploadeds) {
            String fieldName = fileUploaded.getFieldname();
            try {
                Field field = ClassUtils.getDecaredField(this.getClass(), fieldName);
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (FileUploaded.class.isAssignableFrom(fieldType)) {
                    field.set(this, fileUploaded);
                }
                else {
                    String msg = "Invocation " + this.getClass().getName() + " 's field " + field.getName() + " is not a type " + FileUploaded.class.getName();
                    logger.warn(msg);
                    throw new InvocationException(msg);
                }
            }
            catch (NoSuchFieldException e) {
                String msg = "Set invocation " + this.getClass().getName() + "'s FileUploaded field " + fieldName + " with value " + fileUploaded + " failed!";
                logger.warn(msg, e);
                throw new InvocationException(msg, e);
            }
            catch (IllegalAccessException e) {
                String msg = "Set invocation " + this.getClass().getName() + "'s FileUploaded field " + fieldName + " with value " + fileUploaded + " failed!";
                logger.warn(msg, e);
                throw new InvocationException(msg, e);
            }
        }

        //TODO: 检查是否有必须的field还没有设置！！！
    }

    public final Set<String> attributeKeys(){
        return attributes.keySet();
    }

    /**
     * 返回数组
     * @param key key
     */
    public final String[] getAttributeValues(String key) {
        return attributes.get(key);
    }

    /**
     * 如果数组大于1，则返回数组，如果 ==1，返回第一个元素
     * @param key key
     */
    public final Object getAttribute(String key) {
        Object obj = attributes.get(key);
        if(obj != null && obj.getClass().isArray()){
            Object[] objArray = (Object[])obj;
            if(objArray.length == 0){
                return null;
            }
            else if(objArray.length == 1) {
                return objArray[0];
            }
            else {
                return obj;
            }
        }
        else {
            return obj;
        }
    }

    public final String getRequestToken() {
        return request_token;
    }

    public final void setRequestToken(String requestToken) {
        this.request_token = requestToken;
    }

    private void initValidationMap(){
        //构造 fieldMap & validationMap
        Field[] allFields = ClassUtils.getAllDecaredFields(this.getClass());
        for(Field field : allFields){
            if(fieldMap.containsKey(field.getName())){
                logger.warn("Reduplicate filed name: " + field.getName() + " in invocation: " + this.getClass().getName());
                continue;
            }
            fieldMap.put(field.getName(), field);
            Annotation validatorAnnotation = getAvailableValidationAnnotation(field);
            if(validatorAnnotation != null) {
                validationMap.put(field.getName(), validatorAnnotation);
            }

        }
    }

    /**
     * 对应的 @ActionMethod name
     * @throws ValidateException validate exception
     */
    public void validateAll() throws ValidateException {

    }

    private Annotation getAvailableValidationAnnotation(Field field){
        int count = 0;
        Annotation validAnnotation = null;
        Annotation[] fieldAnnotations = field.getAnnotations();
        for(Annotation annotation : fieldAnnotations){
            if(Validators.isValidationAnnotation(annotation)) {
                validAnnotation = annotation;
                count++;
            }
        }
        if(count == 0) {
            return null;
        }
        else if(count > 1){
            logger.warn("More than one Validation Annotation on " + field + ", will use last one.");
            return validAnnotation;
        }
        else {
            return validAnnotation;
        }
    }

    public static void main(String[] args) {

    }
}
