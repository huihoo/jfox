package org.jfox.framework.component;

/**
 * InstantiatedComponent 提供了两个回调方法
 * instantiated方法是唯一可以传入ComponentContext的回调方法，
 * 如果Component需要使用ComponentContex，就必须实现该接口
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentInstantiation extends Component{

    /**
     * Component 实例化之后的回调方法
     * 可以做实例化之后，set Property 之前的准备工作
     * @param componentContext Component context
     */
    public void postContruct(ComponentContext componentContext);

    /**
     * Component 属性设置完毕之后的回调方法
     * 负责做Properties Set 之后的检查工作，以及做 init 操作
     */
    public void postPropertiesSet();

}
