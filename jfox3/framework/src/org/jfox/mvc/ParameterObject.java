/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import code.google.jcontainer.util.ClassUtils;
import code.google.webactioncontainer.validate.ValidateResult;
import org.apache.log4j.Logger;
import code.google.webactioncontainer.invocation.ParseParameterActionInvocationHandler;
import code.google.webactioncontainer.validate.ValidateException;
import code.google.webactioncontainer.validate.Validators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MVC Invocation
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class ParameterObject {

    /**
     * 存的原始数据，从 HttpRequest.parameterMap 复制过来，比如：对于 upload field，这里存的只是 filedname
     * value 统一用数组保存，这样可以处理 checkbox
     */
    private Map<String, String[]> attributes = new HashMap<String, String[]>();

    private List<ValidateResult> errorValidates = new ArrayList<ValidateResult>();

    /**
     * form 中需要有 <input type="hidden" name="request_token" value="$J_REQUESET_TOKEN"> 
     */
    private String request_token = null;

    private Logger logger = Logger.getLogger(this.getClass());

    public ParameterObject() {
//        initValidationMap();
    }

    public void addAttributes(Map<String, String[]> attributes){
        this.attributes.putAll(attributes);
    }

    public void addErrorValidates(ValidateResult validateResult){
        errorValidates.add(validateResult);
    }

    public boolean hasValidateError(){
        return !errorValidates.isEmpty();
    }

    public List<ValidateResult> getErrorValidates(){
        return Collections.unmodifiableList(errorValidates);
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
     *  validate all parameters after ParameterObject inited
     * @throws ValidateException validate exception
     */
    public void validateAll() throws ValidateException {

    }

    public String toString() {
        // 实现 toString，便于日志记录
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        int i=0;
        for(Map.Entry<String,String[]> entry : attributes.entrySet()){
            String key = entry.getKey();
            if(i>0) {
                sb.append(",");
            }
            sb.append(key).append("=");
            String[] value = entry.getValue();
            if(value == null) {
                sb.append("null");
            }
            else if(value.length == 0) {
                sb.append("");
            }
            else if(value.length == 1) {
                sb.append(value[0]);
            }
            else {
                sb.append(Arrays.toString(value));
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) {

    }
}
