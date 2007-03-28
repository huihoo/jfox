package org.jfox.framework.component;

/**
 * 工厂类型的Component，用来创建其它对象
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface FactoryComponent extends Component {

    Component getComponent();

}
