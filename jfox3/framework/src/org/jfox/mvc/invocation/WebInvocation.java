package code.google.webactioncontainer.invocation;

import code.google.jcontainer.AbstractContainer;
import code.google.jcontainer.ComponentMeta;
import code.google.jcontainer.invoke.Invocation;
import code.google.webactioncontainer.ActionContext;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class WebInvocation extends Invocation {

    private ActionContext actionContext;

    public WebInvocation(AbstractContainer container, ComponentMeta componentMeta, Object component, Method method, Object[] parameters) {
        super(container, componentMeta, component, method, parameters);
    }

    public void setActionContext(ActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public ActionContext getActionContext() {
        return actionContext;
    }
}
