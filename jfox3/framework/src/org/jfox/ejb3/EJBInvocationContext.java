package org.jfox.ejb3;

import org.jfox.ejb3.security.SecurityContext;
import org.jfox.mvc.SessionContext;

/**
 * 在EJB调用中保存上下文信息，如和 HttpSession 的关联，SecurityContext等
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class EJBInvocationContext {

    SessionContext sessionContext;

    public EJBInvocationContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public SessionContext getHttpSessionContext() {
        return sessionContext;
    }

    public SecurityContext getSecurityContext(){
        return sessionContext.getSecurityContext();
    }

    public Object getSessionAttribute(String attribute){
        return getHttpSessionContext().getAttribute(attribute);
    }

    public static void main(String[] args) {

    }
}
