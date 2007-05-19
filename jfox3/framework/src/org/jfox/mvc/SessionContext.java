/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jfox.ejb3.security.SecurityContext;

/**
 * Session 上下文，用来存放 Session 数据
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SessionContext implements Serializable {
    
    public static final String SESSION_KEY = "__SESSION_KEY__";
    public static final String SECURITY_CONTEXT_SESSION_KEY = "__SECURITY_SUBJECT__";

    private Map<Serializable, Serializable> sessionMap = new HashMap<Serializable, Serializable>();

    static ThreadLocal<SessionContext> threadSession = new ThreadLocal<SessionContext>();

    private SessionContext() {
    }

    /**
     * 使用 request 初始化 session context，
     * 初始化完毕之后，将 session context 关联到当前线程
     * @param request http request
     */
    public static SessionContext init(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        SessionContext sessionContext = (SessionContext)request.getSession().getAttribute(SESSION_KEY);
        if (sessionContext == null) {
            sessionContext = new SessionContext();
            request.getSession().setAttribute(SESSION_KEY, sessionContext);
        }
        threadSession.set(sessionContext);
        return sessionContext;
    }

    public static SessionContext currentThreadSessionContext(){
        // return null, if current thread not associate session context
        return threadSession.get();
    }

    public static void disassociateThreadSessionContext(){
        threadSession.remove();
    }

    /**
     * 在Session中绑定SecurityContext
     * @param securityContext 认证完成之后生成的SecurityContext
     */
    public void bindSecurityContext(SecurityContext securityContext){
        this.setAttribute(SECURITY_CONTEXT_SESSION_KEY, securityContext);
    }

    /**
     * 取得当前Session关联的SecurityContext
     */
    public SecurityContext getSecurityContext(){
        return (SecurityContext)this.getAttribute(SECURITY_CONTEXT_SESSION_KEY);
    }

    public void setAttribute(Serializable key, Serializable value) {
        sessionMap.put(key,value);
    }

    public Serializable getAttribute(Serializable key) {
        return sessionMap.get(key);
    }

    public boolean containsAttribute(String key){
        return sessionMap.containsKey(key);
    }

    public Serializable removeAttribute(String key) {
        return sessionMap.remove(key);
    }

    public void destroy(){
        sessionMap.clear();
    }

    public static void main(String[] args) {

    }
}
