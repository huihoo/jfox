package code.google.webactioncontainer.invocation;

import code.google.jcontainer.invoke.Invocation;
import code.google.jcontainer.invoke.InvocationHandler;
import code.google.webactioncontainer.ActionContext;
import code.google.webactioncontainer.ActionResubmitException;
import code.google.webactioncontainer.ParameterObject;
import code.google.webactioncontainer.SessionContext;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 1:19:39 PM
 */
public class CheckSessionActionInvocationHandler implements InvocationHandler {

    public void chainInvoke(Invocation invocation) throws Exception {
        ActionContext actionContext = (ActionContext)invocation.getParameters()[0];
        actionContext.initThreadActionContext();
        checkSessionToken(actionContext);
    }

    public void chainReturn(Invocation invocation) throws Exception {
        ActionContext actionContext = (ActionContext)invocation.getParameters()[0];
        releaseSessionToken(actionContext);
        actionContext.disassociateThreadActionContext();
    }

    public void onCaughtException(Invocation invocation, Exception e) {
        invocation.onCaughtException(e);
    }

    protected void checkSessionToken(ActionContext actionContext) {
        ParameterObject parameterObject = actionContext.getParameterObject();
        if (parameterObject.getRequestToken() != null) {
            SessionContext sessionContext = actionContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + parameterObject.getRequestToken();
            if (sessionContext.containsAttribute(key)) {
                // token 已经存在，是重复提交
                throw new ActionResubmitException("Detected re-submit, action: " + actionContext.getFullActionMethodName() + ", token: " + parameterObject.getRequestToken());
            }
            else {
                sessionContext.setAttribute(key, "1");
            }
        }
    }

    protected void releaseSessionToken(ActionContext actionContext) {
        ParameterObject parameterObject = actionContext.getParameterObject();
        if (parameterObject != null && parameterObject.getRequestToken() != null) {
            SessionContext sessionContext = actionContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + parameterObject.getRequestToken();
            sessionContext.removeAttribute(key);
        }
    }

}