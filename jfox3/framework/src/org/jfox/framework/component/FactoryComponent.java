/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

/**
 * 工厂类型的Component，用来创建其它对象
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface FactoryComponent extends Component {

    Component getComponent();

}
