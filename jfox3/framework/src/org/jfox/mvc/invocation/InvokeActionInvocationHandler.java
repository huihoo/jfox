package org.jfox.mvc.invocation;

import org.jfox.mvc.Action;
import org.jfox.mvc.ActionBucket;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionInvocationHandler;
import org.jfox.mvc.PageContext;

import java.util.Iterator;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 1:19:39 PM
 */
public class InvokeActionInvocationHandler extends ActionInvocationHandler {

    public PageContext invoke(ActionContext actionContext, Iterator<ActionInvocationHandler> chain) throws Exception {
        ActionBucket actionBucket = actionContext.getActionBucket();
        Action actionObject = (Action)actionBucket.getActionObject();
//        ActionMethod actionMethodAnnotation = actionContext.getActionMethodAnnotation();
//        String successView = actionMethodAnnotation.successView();
        // 没有设置 errorView，将会直接抛出异常
//        String errorView = actionMethodAnnotation.errorView();
        //设置跳转方式已经在 ParseParameterActionInvocationHandler 中设置好
//        actionContext.getPageContext().setTargetMethod(actionMethodAnnotation.forwardMethod());
        //设置目标模板页面
//        actionContext.getPageContext().setTargetView(successView);

        actionObject.execute(actionContext);
/*
        try {
            actionObject.execute(actionContext);

        }
        catch (Exception e) { // 处理异常
            
        }
*/
        return actionContext.getPageContext();

/*
        Exception exception = null;
        try {
            actionMethod.invoke(actionObject, actionContext);
        }
        catch (InvocationTargetException e) { // exception throwed, forward to error view
                    Exception t = (Exception)e.getTargetException();
                    logger.warn("Execute Action Method " + actionMethod.getName() + " throws exception.", t);
                    actionContext.getPageContext().setTargetView(errorView);
                    actionContext.getPageContext().setBusinessException(t);
                    actionObject.doActionFailed(actionContext);
                    exception = t;
        }
        catch (Exception e) { // exception throwed, forward to error view
            logger.warn("Execute Action Method " + actionMethod.getName() + " throws exception.", e);
            actionContext.getPageContext().setTargetView(errorView);
            actionContext.getPageContext().setBusinessException(e);
            actionObject.doActionFailed(actionContext);
            exception = e;
        }
        finally {
            try {
                if (exception != null) {
                    actionObject.doActionFailed(actionContext);
                }
            }
            finally {
                actionObject.postAction(actionContext);
//TODO:                releaseSessionToken(actionContext);
            }
            actionObject.postExecute(actionContext);
        }
        // 没有设置 errorView, 抛出异常
        if (exception != null && (errorView == null || errorView.trim().length() == 0)) {
            throw exception;
        }
        return actionContext.getPageContext();

*/

    }

    public static void main(String[] args) {

    }
}