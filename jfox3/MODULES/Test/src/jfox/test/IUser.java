/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test;

import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Exported
public interface IUser extends Component, ComponentInitialization {

    String getName();

}
