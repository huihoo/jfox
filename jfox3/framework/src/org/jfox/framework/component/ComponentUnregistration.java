package org.jfox.framework.component;

/**
 * 组件被注销之前和注销之后的回调方法。
 * 
 * 组件注册的时候，没有回调方法，因为此时组件还没有实例化。
 *
 * @See ComponentInitialization
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentUnregistration {

    /**
     * 组件被注销之前的回调方法
     *
     * @param context component context
     * @return true if can be unregister, false can not be unregister
     */
     boolean preUnregister(ComponentContext context);

    /**
     * 组件被注销之后的回调方法，进行注销之后的资源清除操作。
     */
     void postUnregister();
}
