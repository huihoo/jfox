package net.sourceforge.jfox.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jfox.mvc.validate.ValidateException;

/**
 * Page Context 存储了用来填充 template的 数据
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class PageContext {

    // success or error view
    private String targetView;

    // validate exception
    private List<ValidateException> validateExceptions = new ArrayList<ValidateException>();

    private Map<String,Object> resultMap = new HashMap<String, Object>();

    private Exception businessException = null;

    PageContext() {

    }

    public Map<String,Object> getResultMap(){
        return resultMap;
    }

    public void setTargetView(String targetView) {
        this.targetView = targetView;
    }

    public String getTargeView(){
        return targetView;
    }

    public void setAttribute(String key, Object value) {
        resultMap.put(key,value);
    }

    public Object getAttribute(String key) {
        return resultMap.get(key);
    }

    public void addValidateException(ValidateException ve){
        validateExceptions.add(ve);
    }

    public boolean hasValidateException(){
        return !validateExceptions.isEmpty();
    }

    public void setBusinessException(Exception exception){
        this.businessException = exception;
    }

    public boolean hasBusinessException(){
        return businessException != null;
    }

    public Exception getBusinessException(){
        return businessException;
    }

    public List<ValidateException> getValidateExceptions(){
        return validateExceptions;
    }

    public static void main(String[] args) {

    }
}
