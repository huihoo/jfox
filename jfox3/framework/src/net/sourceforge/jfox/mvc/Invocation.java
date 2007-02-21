package net.sourceforge.jfox.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jfox.mvc.validate.ValidateException;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class Invocation {

    /**
     * 存的原始数据，从 HttpRequest.parameterMap 复制过来，比如：对于 upload field，这里存的只是 filedname
     * value 统一用数组
     */
    private Map<String, String[]> attributes = new HashMap<String, String[]>();

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
        if(obj.getClass().isArray()){
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

    public static void main(String[] args) {

    }
}
