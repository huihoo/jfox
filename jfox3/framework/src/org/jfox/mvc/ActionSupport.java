/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import code.google.webactioncontainer.validate.ValidateResult;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * WebAction 超类
 * <p/>
 * 一个Action类可以有多个Action方法来响应HTTP请求，子类的方法用 @ActionMethod 标注为一个Action方法
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class ActionSupport implements Action {

    public static final String PAGE_VIEW_PATH = "J_PAGE_VIEW";

    protected Logger logger = Logger.getLogger(this.getClass());

    public Logger getLogger() {
        return logger;
    }

    private void setBackPageContext(ActionContext actionContext){
        PageContext pageContext = actionContext.getPageContext();
        for(String key : actionContext.getParameterMap().keySet()) {
            pageContext.setAttribute(key, actionContext.getParameterValue(key));
        }

/* file input can not be set by value
        for(FileUploaded fileUploaded : actionContext.getFilesUploaded()) {
            pageContext.setAttribute(fileUploaded.getFieldname(), fileUploaded.getFilename());
        }
*/

    }

    public final void execute(ActionContext actionContext) throws Exception {
        PageContext pageContext = actionContext.getPageContext();
        Method actionMethod = actionContext.getActionMethod();

        String successView = actionContext.getSuccessView();
        // default set targetView to successView
        actionContext.getPageContext().setTargetView(successView);

        // 没有设置 errorView，将会直接抛出异常
        String errorView = actionContext.getErrorView();
        boolean hasErrorView = (errorView != null && errorView.trim().length() != 0);

        // check validate error
        ParameterObject parameterObject = actionContext.getParameterObject();
        if(parameterObject.hasValidateError()) {
            List<ValidateResult> errorValidates = parameterObject.getErrorValidates();
            for(ValidateResult entry : errorValidates){
                String attrKey = "J_VALIDATE_ERROR_" + entry.getInputField();
                // ActionSupport.validateAll may overwrite the existed key, so use the first error message
                if(!pageContext.hasAttribute(attrKey)) {
                    pageContext.setAttribute(attrKey,entry.getErrorMessage());
                }
            }
            if(hasErrorView) {
                pageContext.setTargetView(actionContext.getErrorView());
            }
            else {
                throw new ActionException("Failed to validate input." + Arrays.toString(errorValidates.toArray(new ValidateResult[errorValidates.size()])));
            }
            setBackPageContext(actionContext);
            return;
        }

        Exception exception = null;
        try {
            // pre action
            preAction(actionContext);

            // 判断执行权限
            if (!hasPermission(actionContext)) {
                // hasPermission 返回 false, 这里统一抛出异常
                throw new PermissionNotAllowedException(actionContext.getFullActionMethodName(), "Unknown");
            }

            // invoke action method
            actionMethod.invoke(this, actionContext);
        }
        catch (InvocationTargetException e) { // exception throwed, forward to error view
            Exception t = (Exception) e.getTargetException();
            logger.warn("Execute WebAction Method " + actionMethod.getName() + " throws exception.", t);
            pageContext.setTargetView(errorView);
            pageContext.setBusinessException(t);
//            doActionFailed(actionContext);
            exception = t;
        }
        catch (Exception e) { // exception throwed, forward to error view
            logger.warn("Execute WebAction Method " + actionMethod.getName() + " throws exception.", e);
            pageContext.setTargetView(errorView);
            pageContext.setBusinessException(e);
//            doActionFailed(actionContext);
            exception = e;
        }
        finally {
            setBackPageContext(actionContext);
            postAction(actionContext);
        }

        // throw exception to web container
        if (exception != null && !hasErrorView) {
            throw exception;
        }
    }


    /**
     * 判断当前用户是否有权限操作该Action
     *
     * @param actionContext actionContext
     * @return true/false
     * @throws PermissionNotAllowedException 如果不具备权限，抛出异常，如果不抛出异常，直接返回 false, ActionSupport统一抛出异常
     */
    protected boolean hasPermission(ActionContext actionContext) throws PermissionNotAllowedException {
        return true;
    }

    /**
     * do something before invoke action method
     *
     * @param actionContext invcation context
     */
    protected void preAction(ActionContext actionContext) {
        PageContext pageContext = actionContext.getPageContext();

        HttpServletRequest request = actionContext.getServletRequest();

        // request token，用来防止重复提交
        pageContext.setAttribute("J_REQUEST_TOKEN", System.currentTimeMillis() + "");
    }

    /**
     * do something after invocation action method
     *
     * @param actionContext invocation context
     */
    protected void postAction(ActionContext actionContext) {
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute(PAGE_VIEW_PATH, pageContext.getTargeView());
        pageContext.setAttribute("J_EXCEPTION", pageContext.getBusinessException());
        pageContext.setAttribute("J_PARAMETER_OBJECT", actionContext.getParameterObject());
    }

    /**
     * 在 execute 过程中出现异常时，将回调该方法。
     * 该方法可以做一些补偿性工作，比如：在 buildInvocation 过程中出现了异常，
     * 可以通过实现该方法恢复数据，以便能够在 errorView中显示用户数据
     *
     * @param actionContext invocationContext
     */
    protected void doActionFailed(ActionContext actionContext) {

    }

}
