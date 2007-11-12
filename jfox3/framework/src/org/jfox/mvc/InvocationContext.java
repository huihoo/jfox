/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * 调用上下文，封装调用过程中的所有资源
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InvocationContext {

    private String actionName;

    /**
     * ActionMethod名称
     */
    private String actionMethodName;

    /**
     * 执行的Action方法
     */
    private Method actionMethod = null;

    /**
     * 表单 和 QueryString 提交的数据
     */
    private Map<String, String[]> parameterMap = new HashMap<String, String[]>();

    /**
     * 上传的文件 fieldname=>FileUploaded
     */
    private Map<String, FileUploaded> fileUploadedMap = new HashMap<String, FileUploaded>();

    private PageContext pageContext;
    private SessionContext sessionContext;
    private Invocation invocation;

    private ServletConfig servletConfig;

    private HttpServletRequest request = null;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    public static final String SESSION_KEY = "__SESSION_KEY__";

    /**
     * 使用 ThreadLocal 将 InvocationContext 和当前线程进行关联
     */
    static transient ThreadLocal<InvocationContext> threadInvocationContext = new ThreadLocal<InvocationContext>();
    

    public InvocationContext(ServletConfig servletConfig, HttpServletRequest request, Map<String, String[]> parameterMap, Map<String, FileUploaded> fileUploadedMap, String actionName, String actionMethodName) {
        this.servletConfig = servletConfig;
        this.request = request;
        this.parameterMap.putAll(parameterMap);
        this.fileUploadedMap.putAll(fileUploadedMap);
        this.actionName = actionName;
        this.actionMethodName = actionMethodName;
        this.pageContext = new PageContext();
    }

    /**
     * 得到当前线程绑定的 InvocationContext
     */
    public static InvocationContext getCurrentThreadInvocationContext(){
        return threadInvocationContext.get();
    }

    public void initInvocationContext(){
        this.sessionContext = initSessionContext();
        threadInvocationContext.set(this);
    }

    public void disassociateThreadInvocationContext(){
        threadInvocationContext.remove();
    }

    /**
     * 使用 request 初始化 session context，
     * 初始化完毕之后，将 session context 关联到当前线程
     */
    private SessionContext initSessionContext() {
        if(request == null) {
            return null;
        }
        SessionContext sessionContext = (SessionContext)request.getSession().getAttribute(SESSION_KEY);
        if (sessionContext == null) {
            sessionContext = new SessionContext();
            request.getSession().setAttribute(SESSION_KEY, sessionContext);
        }
        return sessionContext;
    }

    HttpServletRequest getServletRequest() {
        return request;
    }

    /**
     * 销毁 Session
     */
    public void destroySessionContext(){
        getSessionContext().clearAttributes();
        request.getSession().removeAttribute(SESSION_KEY);
    }

    public String getRequestURI(){
        return getServletRequest().getRequestURI();
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public ServletContext getServletContext(){
        return servletConfig.getServletContext();
    }

    void setActionMethod(Method actionMethod) {
        this.actionMethod = actionMethod;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    public String getActionMethodName() {
        return actionMethodName;
    }

    public String getFullActionMethodName(){
        return getActionName() + "." + getActionMethodName();
    }

    public String getActionName(){
        return actionName;
    }

    public boolean isPost(){
        return "POST".equals(getServletRequest().getMethod().toUpperCase());
    }

    public SessionContext getSessionContext(){
        return sessionContext;
    }

    void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public PageContext getPageContext(){
        return pageContext;
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

    public Collection<FileUploaded> getFilesUploaded() {
        return fileUploadedMap.values();
    }

    public FileUploaded getFileUploaded(String fieldname) {
        return fileUploadedMap.get(fieldname);
    }

    public String getRemoteAddress(){
        return getServletRequest().getRemoteAddr();
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean constainsKey(String key){
        return attributes.containsKey(key);
    }

    public boolean removeAttribute(String key) {
        return attributes.remove(key) != null;
    }

    public static void main(String[] args) {

    }
}
