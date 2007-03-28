package org.jfox.framework.component;

/**
 * callback of component unregister
 * 
 * no callback for component register,
 * 因为 register 的时候，是 register ComponentMeta，component 还没有实例化
 *
 * @See ComponentInstantiation 
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentUnregistration {

    /**
     * callback of brefore unregister
     *
     * @param context component context
     */
     void preUnregister(ComponentContext context);

    /**
     * callback of after unregistered
     */
     void postUnregister();
}
