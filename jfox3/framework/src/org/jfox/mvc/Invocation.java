/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jfox.mvc.validate.ValidateException;

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
     * form 中需要有 <input type="hidden" name="request_token" value="$J_REQUESET_TOKEN"> 
     */
    private String request_token = null;

    final void putAll(Map<String, String[]> parameterMap) {
        attributes.putAll(parameterMap);
    }

    public Set<String> attributeKeys(){
        return attributes.keySet();
    }

    /**
     * 返回数组
     * @param key key
     */
    public String[] getAttributeValues(String key) {
        return attributes.get(key);
    }

    /**
     * 如果数组大于1，则返回数组，如果 ==1，返回第一个元素
     * @param key key
     */
    public Object getAttribute(String key) {
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

    /**
     * 对应的 @ActionMethod name
     * @throws ValidateException validate exception
     */

    public void validateAll() throws ValidateException {
        
    }

    public String getRequestToken() {
        return request_token;
    }

    public void setRequestToken(String requestToken) {
        this.request_token = requestToken;
    }

    public static void main(String[] args) {

    }
}
