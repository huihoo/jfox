/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import org.jfox.mvc.annotation.ActionMethod;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用上下文，封装调用过程中的所有资源
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ActionContext {

    private String moduleName;

    private String actionName;

    /**
     * ActionMethod名称
     */
    private String actionMethodName;

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

    private ActionBucket actionBucket;

    /**
     * 使用 ThreadLocal 将 InvocationContext 和当前线程进行关联
     */
    static transient ThreadLocal<ActionContext> threadActionContext = new ThreadLocal<ActionContext>();


    public ActionContext(ServletConfig servletConfig, String moduleName, String actionName, String actionMethodName, HttpServletRequest request) {
        this.servletConfig = servletConfig;
        this.moduleName = moduleName;
        this.actionName = actionName;
        this.actionMethodName = actionMethodName;
        this.request = request;
        this.pageContext = new PageContext();
    }

    public String getModuleName() {
        return moduleName;
    }

    public ActionContext(ServletConfig servletConfig, HttpServletRequest request, Map<String, String[]> parameterMap, Map<String, FileUploaded> fileUploadedMap, String actionName, String actionMethodName) {
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
    public static ActionContext getCurrentThreadActionContext(){
        return threadActionContext.get();
    }

    public void initThreadActionContext(){
        this.sessionContext = initSessionContext();
        threadActionContext.set(this);
    }

    public void disassociateThreadActionContext(){
        threadActionContext.remove();
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

    public HttpServletRequest getServletRequest() {
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

    public Method getActionMethod() {
        Method actionMethod = getActionBucket().getActionMethod(this);
        if(actionMethod == null) {
            throw new ActionMethodNotFoundException(getActionMethodName(), getActionName(), getModuleName());
        }
        return actionMethod;
    }

    public ActionMethod getActionMethodAnnotation(){
        return getActionMethod().getAnnotation(ActionMethod.class);
    }

    public String getSuccessView(){
        return getActionMethodAnnotation().successView();
    }

    public String getErrorView(){
        return getActionMethodAnnotation().errorView();
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

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public Class<? extends Invocation> getInvocationClass(){
        return getActionMethod().getAnnotation(ActionMethod.class).invocationClass();
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

    public void setParameterMap(Map<String, String[]> parameterMap) {
        this.parameterMap.clear();
        this.parameterMap.putAll(parameterMap);
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public void setFileUploadedMap(Map<String, FileUploaded> fileUploadedMap) {
        this.fileUploadedMap.clear();
        this.fileUploadedMap.putAll(fileUploadedMap);
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


    public boolean isMultipartContent() {
        if (!"POST".equals(request.getMethod().toUpperCase())) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    public ActionBucket getActionBucket() {
        return actionBucket;
    }

    public void setActionBucket(ActionBucket actionBucket) {
        this.actionBucket = actionBucket;
    }

    public static void main(String[] args) {

    }
}
