/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.validate.ValidateException;

/**
 * Page Context 存储了用来填充 template的 数据
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class PageContext {

    //页面跳转方式，foward/redirect
    private ActionMethod.TargetMethod targetMethod;

    // success or error view
    private String targetView;

    // input field => validate exception
    private Map<String, ValidateException> validateExceptions = new HashMap<String, ValidateException>();

    private Map<String,Object> resultMap = new HashMap<String, Object>();

    private Exception businessException = null;

    PageContext() {

    }

    void setTargetMethod(ActionMethod.TargetMethod targetMethod) {
        this.targetMethod = targetMethod;
    }

    public ActionMethod.TargetMethod getTargetMethod() {
        return targetMethod;
    }

    public Map<String,Object> getResultMap(){
        return Collections.unmodifiableMap(resultMap);
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
        validateExceptions.put(ve.getInputField(),ve);
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

    public String getBusinessExceptionMessage(){
        StringWriter sw = new StringWriter();
        businessException.printStackTrace(new PrintWriter(sw));
        try {
            sw.close();
        }
        catch(Exception e) {
            // ignore
        }
        return sw.getBuffer().toString();
    }

    public Map<String, ValidateException> getValidateExceptions(){
        return Collections.unmodifiableMap(validateExceptions);
    }

    public static void main(String[] args) {

    }
}
