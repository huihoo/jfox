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

import org.apache.log4j.Logger;
import org.jfox.mvc.validate.ValidateException;
import org.jfox.mvc.validate.Validators;
import org.jfox.util.ClassUtils;

/**
 * MVC Invocation
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class Invocation {

    /**
     * 存的原始数据，从 HttpRequest.parameterMap 复制过来，比如：对于 upload field，这里存的只是 filedname
     * value 统一用数组保存，这样可以处理 checkbox
     */
    private Map<String, String[]> attributes = new HashMap<String, String[]>();

    /**
     * form 中需要有 <input type="hidden" name="request_token" value="$J_REQUESET_TOKEN"> 
     */
    private String request_token = null;

    private Logger logger = Logger.getLogger(this.getClass());

    public Invocation() {
//        initValidationMap();
    }

    /**
     * 设置所有数据，并进行校验
     * @param fieldValidationMap fieldValidationMap
     * @param parameterMap 提交的 http request parameterMap
     * @param fileUploadeds 上传的文件
     * @throws ValidateException valiate
     * @throws InvocationException invocation
     */
    final void init(Map<String, ActionSupport.FieldValidation> fieldValidationMap, Map<String, String[]> parameterMap, Collection<FileUploaded> fileUploadeds) throws ValidateException, InvocationException {

        attributes.putAll(parameterMap);
        // verify & build form field from parameterMap
        ValidateException validateException = null;

        // 复制一份
        fieldValidationMap = new HashMap<String, ActionSupport.FieldValidation>(fieldValidationMap);        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            try {
                ActionSupport.FieldValidation fieldValidation = fieldValidationMap.remove(key);
                if(fieldValidation == null) {
                    //仅仅发出一个信息
                    String msg = "Set invocation " + this.getClass().getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed, No such filed!";
                    logger.warn(msg);
                    continue;
                }
                Field field = fieldValidation.getField();
                field.setAccessible(true);
                Annotation validationAnnotation = fieldValidation.getValidationAnnotation();

                Class<?> fieldType = field.getType();
                if (fieldType.isArray()) {
                    Class<?> arrayType = fieldType.getComponentType();
                    Object[] params = (Object[])Array.newInstance(arrayType, values.length);
                    for (int i = 0; i < params.length; i++) {
                        if (validationAnnotation != null) {
                            try {
                                // valiate field input and construct
                                params[i] = Validators.validate(field, values[i], validationAnnotation);
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

                    if (validationAnnotation != null) {
                        try {
                            v = Validators.validate(field, value, validationAnnotation);
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

        // 检查是否有必须的field还没有设置
        for(ActionSupport.FieldValidation fieldValidation : fieldValidationMap.values()){
            Annotation validationAnnotation = fieldValidation.getValidationAnnotation();
            if(validationAnnotation != null) {
                if(!Validators.isValidationNullable(validationAnnotation)){
                    validateException = new ValidateException("input can not be null!", fieldValidation.getField().getName(), null);
                    break;
                }
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


    /**
     * 对应的 @ActionMethod name
     * @throws ValidateException validate exception
     */
    public void validateAll() throws ValidateException {

    }

    public String toString() {
        //TODO: 实现 toString，便于日志记录
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    public static void main(String[] args) {

    }
}
