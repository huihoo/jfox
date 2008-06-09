package org.jfox.mvc.invocation;

import org.jfox.mvc.Action;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionInvocationHandler;
import org.jfox.mvc.PageContext;

import java.util.Iterator;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 1:19:39 PM
 */
public class CheckPermissionActionInvocationHandler extends ActionInvocationHandler {

    //TODO: Check Permission 要在 preAction 之后运行，所以，不宜独立出来，还是有 ActionSupport来做合适

    public PageContext invoke(ActionContext actionContext, Iterator<ActionInvocationHandler> chain) throws Exception {
        Action actionObject = (Action)actionContext.getActionBucket().getActionObject();
/*
        if(actionObject instanceof ActionSupport) {
            if(!((ActionSupport)actionObject).hasPermission(actionContext)){
                // hasPermission 返回 false, 这里统一抛出异常
                throw new PermissionNotAllowedException(actionContext.getFullActionMethodName(), "Unknown");
            }
        }
*/

        return super.next(actionContext, chain);
    }

}
