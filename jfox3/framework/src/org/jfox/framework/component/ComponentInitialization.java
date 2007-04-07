package org.jfox.framework.component;

/**
 * ComponentInitialization 提供了两个回调方法，
 * 使得在组件构造之后，以及依赖注入完成之后进行额外的操作。
 * 
 * instantiated方法是唯一可以传入ComponentContext的回调方法，
 * 如果Component需要使用ComponentContex，就必须实现该接口
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentInitialization {

    /**
     * Component 实例化之后的回调方法
     * 可以做依赖注入开始前的初始化操作
     * @param componentContext Component context
     */
    public void postContruct(ComponentContext componentContext);

    /**
     * Component 依赖注入完成之后的回调方法
     * 可以做通过该方法进行注入之后额外的检查工作，以及做组件初始化操作
     */
    public void postInject();

}
