package net.sourceforge.jfox.mvc;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SessionContext implements Serializable {

    private Map<Serializable, Serializable> sessionMap = new HashMap<Serializable, Serializable>();

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
