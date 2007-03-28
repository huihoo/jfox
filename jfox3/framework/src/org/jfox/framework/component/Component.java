package org.jfox.framework.component;

/**
 * Component 接口，只有实现该接口的组件才能部署到 Framework 中
 * 普通的Component不具备跨模块调用能力，要想被其它模块调用，需要用 @Exported描述一个 business 接口
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface Component {

    /**
     * set component context，以便能够通过 ComponentContext 获得相关资源
     * Framework 会在一个 Component 注册的时候回调该方法
     *
     * @param compContext ComponentContext
     */
//    public void setComponentContext(ComponentContext compContext);

}