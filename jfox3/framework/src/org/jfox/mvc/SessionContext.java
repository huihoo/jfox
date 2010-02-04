/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Session 上下文，用来存放 Session 数据
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SessionContext implements Serializable {
    
    public static final String SECURITY_CONTEXT_SESSION_KEY = "__SECURITY_SUBJECT__";

    public static final String TOKEN_SESSION_KEY ="SESSION_TOKEN.";

    /**
     * 使用Map存储Session数据
     */
    private Map<String, Serializable> sessionMap = new HashMap<String, Serializable>();

    SessionContext() {
    }

    public static SessionContext getCurrentThreadSessionContext(){
        ActionContext actionContext = ActionContext.getCurrentThreadActionContext();
        if(actionContext != null) {
            return actionContext.getSessionContext();
        }
        else {
            return null;
        }
    }

    public void setAttribute(String key, Serializable value) {
        sessionMap.put(key,value);
    }

    public Serializable getAttribute(String key) {
        return sessionMap.get(key);
    }

    public boolean containsAttribute(String key){
        return sessionMap.containsKey(key);
    }

    public Serializable removeAttribute(String key) {
        return sessionMap.remove(key);
    }

    public String[] getAttributeNames(){
        return sessionMap.keySet().toArray(new String[sessionMap.size()]);
    }

    /**
     * 销毁 SessionContext
     */
    public void clearAttributes(){
        sessionMap.clear();
    }

    public static void main(String[] args) {

    }
}
