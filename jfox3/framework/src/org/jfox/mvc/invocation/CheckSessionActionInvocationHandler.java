package org.jfox.mvc.invocation;

import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionInvocationHandler;
import org.jfox.mvc.ActionResubmitException;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;

import java.util.Iterator;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 1:19:39 PM
 */
public class CheckSessionActionInvocationHandler extends ActionInvocationHandler {

    public PageContext invoke(ActionContext actionContext, Iterator<ActionInvocationHandler> chain) throws Exception {
        try {
            actionContext.initThreadActionContext();
            checkSessionToken(actionContext);
            return super.next(actionContext, chain);
        }
        finally {
            releaseSessionToken(actionContext);
            actionContext.disassociateThreadActionContext();
        }
    }

    protected void checkSessionToken(ActionContext actionContext) {
        Invocation invocation = actionContext.getInvocation();
        if (invocation.getRequestToken() != null) {
            SessionContext sessionContext = actionContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + invocation.getRequestToken();
            if (sessionContext.containsAttribute(key)) {
                // token 已经存在，是重复提交
                throw new ActionResubmitException("Detected re-submit, action: " + actionContext.getFullActionMethodName() + ", token: " + invocation.getRequestToken());
            }
            else {
                sessionContext.setAttribute(key, "1");
            }
        }
    }

    protected void releaseSessionToken(ActionContext actionContext) {
        Invocation invocation = actionContext.getInvocation();
        if (invocation != null && invocation.getRequestToken() != null) {
            SessionContext sessionContext = actionContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + invocation.getRequestToken();
            sessionContext.removeAttribute(key);
        }
    }

}