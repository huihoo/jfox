/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * 调用上下文，封装调用过程中的所有资源
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InvocationContext {

    /**
     * Action名称
     */
    private String actionName;

    /**
     * 是否是http post
     */
    private boolean postMethod = false;

    /**
     * 执行的Action方法
     */
    private Method actionMethod = null;

    /**
     * 表单 和 QueryString 提交的数据
     */
    private Map<String, String[]> parameterMap = new HashMap<String, String[]>();

    /**
     * 上传的文件
     */
    private Map<String, FileUploaded> fileUploadedMap = new HashMap<String, FileUploaded>();

    private PageContext pageContext;
    private SessionContext sessionContext;
    private Invocation invocation;

    private ServletConfig servletConfig;

    private HttpServletRequest request = null;

    public InvocationContext(ServletConfig servletConfig, HttpServletRequest request, String name) {
        this.servletConfig = servletConfig;
        this.request = request;
        this.actionName = name;
        this.pageContext = new PageContext();
    }

    HttpServletRequest getServletRequest() {
        return request;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public ServletContext getServletContext(){
        return servletConfig.getServletContext();
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    void setActionMethod(Method actionMethod) {
        this.actionMethod = actionMethod;
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public String getActionName() {
        return actionName;
    }

    public void setPostMethod(boolean postMethod) {
        this.postMethod = postMethod;
    }

    public boolean isPostMethod(){
        return postMethod;
    }

    public SessionContext getSessionContext(){
        return sessionContext;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public PageContext getPageContext(){
        return pageContext;
    }

    public void addParameter(String key, String[] values){
        parameterMap.put(key, values);
    }

    public String getParameterValue(String key){
        return parameterMap.get(key)[0];
    }

    public String[] getParameterValues(String key){
        return parameterMap.get(key);
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public void addFileUploaded(String key, FileUploaded fileUploaded){
        fileUploadedMap.put(key, fileUploaded);
    }

    public Map<String, FileUploaded> getFileUploadedMap() {
        return fileUploadedMap;
    }

    public FileUploaded getFileUploaded(String key) {
        return fileUploadedMap.get(key);
    }

    public static void main(String[] args) {

    }
}
