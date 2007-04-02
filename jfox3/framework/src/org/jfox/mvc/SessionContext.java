package org.jfox.mvc;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.security.auth.Subject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SessionContext implements Serializable {
    
    public static final String SESSION_KEY = "__SESSION_KEY__";
    public static final String SUBJECT_SESSION_KEY = "__SECURITY_SUBJECT__";

    private Map<Serializable, Serializable> sessionMap = new HashMap<Serializable, Serializable>();

    static ThreadLocal<SessionContext> threadSession = new ThreadLocal<SessionContext>();

    private SessionContext() {
    }

    public static SessionContext init(HttpServletRequest request) {
        SessionContext sessionContext = (SessionContext)request.getSession().getAttribute(SESSION_KEY);
        if (sessionContext == null) {
            sessionContext = new SessionContext();
            request.getSession().setAttribute(SESSION_KEY, sessionContext);
        }
        threadSession.set(sessionContext);
        return sessionContext;
    }

    public static SessionContext currentThreadSessionContext(){
        return threadSession.get();
    }

    public static void removeThreadSessionContext(){
        threadSession.remove();
    }

    public void associateSubject(Subject subject){
        this.setAttribute(SUBJECT_SESSION_KEY, subject);
    }

    public Subject getAssociatedSubect(){
        return (Subject)this.getAttribute(SUBJECT_SESSION_KEY);
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
