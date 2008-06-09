package org.jfox.mvc;

import org.apache.log4j.Logger;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.Module;
import org.jfox.framework.event.ModuleEvent;
import org.jfox.framework.event.ModuleListener;
import org.jfox.framework.event.ModuleLoadingEvent;
import org.jfox.framework.event.ModuleUnloadedEvent;
import org.jfox.mvc.event.ActionLoadedComponentEvent;
import org.jfox.mvc.invocation.CheckSessionActionInvocationHandler;
import org.jfox.mvc.invocation.InvokeActionInvocationHandler;
import org.jfox.mvc.invocation.ParseParameterActionInvocationHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * //TODO: 管理 Action，就像 EJB Container
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 11:15:34 AM
 */
@Service(id = "ActionContainer", singleton = true, active = true)
public class SimpleActionContainer implements ActionContainer, ModuleListener {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private ComponentContext componentContext;

    /**
     * chain to execute action invocation
     */
    private final List<ActionInvocationHandler> invocationChain = new ArrayList<ActionInvocationHandler>();

    // modulename lowercase= > {ActionName=>ActionBucket}
    private final Map<String, Map<String, ActionBucket>> actionBuckets = new HashMap<String, Map<String, ActionBucket>>();

    public SimpleActionContainer() {
        invocationChain.add(new ParseParameterActionInvocationHandler());
        invocationChain.add(new CheckSessionActionInvocationHandler());
        invocationChain.add(new InvokeActionInvocationHandler());
    }

    public void moduleChanged(ModuleEvent moduleEvent) {
        Module module = moduleEvent.getModule();
        if (moduleEvent instanceof ModuleLoadingEvent) {
            ActionBucket[] buckets = loadAction(module);
            Map<String, ActionBucket> moduleActionMap = new HashMap<String, ActionBucket>(buckets.length);
            for (ActionBucket bucket : buckets) {
                moduleActionMap.put(bucket.getActionName(), bucket);
                // start action bucket，将进行实例化并且完成依赖注入
                bucket.start();
            }
            actionBuckets.put(module.getName().toLowerCase(), moduleActionMap);

            //TODO: 初始化所有的 Action
        }
        else if (moduleEvent instanceof ModuleUnloadedEvent) {
            unloadAction(module);
        }
    }

    protected ActionBucket[] loadAction(Module module) {
        List<ActionBucket> buckets = new ArrayList<ActionBucket>();

        // all action classes 
        Class[] actionClasses = module.getModuleClassLoader().findClassAnnotatedWith(org.jfox.mvc.annotation.Action.class);
        for (Class actionClass : actionClasses) {
            ActionBucket bucket = new ActionBucket((ActionContainer)componentContext.getMyselfComponent(), actionClass, module);
            buckets.add(bucket);
            //fireEvent, 以便XFire可以 register Endpoint
            componentContext.fireComponentEvent(new ActionLoadedComponentEvent(componentContext.getComponentId(), bucket));
            logger.info("Action loaded, Action class: " + actionClass.getName() + ", Action name: " + bucket.getActionName());
        }
        return buckets.toArray(new ActionBucket[buckets.size()]);
    }

    protected void unloadAction(Module module) {
        //TODO:
    }

    public void postContruct(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    public void postInject() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean preUnregister(ComponentContext context) {
        return true;
    }

    public void postUnregister() {
        
    }

    public boolean preInvoke(Method method, Object[] params) {
        return true;
    }

    public Object postInvoke(Method method, Object[] params, Object result, Throwable exception) {
        return result;
    }

    public PageContext invokeAction(ActionContext actionContext) throws Exception {
        logger.info("Request accepted, URI: " + actionContext.getRequestURI());
        long now = System.currentTimeMillis();
        String moduleName = actionContext.getModuleName().toLowerCase();
        if(!actionBuckets.containsKey(moduleName)) {
            throw new ModuleNotExistedException(moduleName);
        }
        Map<String, ActionBucket> moduleActionBuckets = actionBuckets.get(moduleName);
        String actionName = actionContext.getActionName();
        if(!moduleActionBuckets.containsKey(actionName)) {
            throw new ActionNotFoundException(moduleName, actionName);
        }
        ActionBucket actionBucket = moduleActionBuckets.get(actionName);
        actionContext.setActionBucket(actionBucket);
        // 构造 ActionContext，然后交给 chain 执行
        Iterator<ActionInvocationHandler> chain = invocationChain.iterator();
        PageContext pageContext = chain.next().invoke(actionContext, chain);
        logger.info("Request done, URI: " + actionContext.getRequestURI() + ", consumed " + (System.currentTimeMillis() - now) + "ms.");
        return pageContext;
    }

    public static void main(String[] args) {
 
    }
}
