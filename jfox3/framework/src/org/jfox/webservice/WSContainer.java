/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.webservice;

import org.jfox.framework.component.Component;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.SingletonComponent;

/**
 * Web Service container interface, now only used to findComponentByInterface  
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface WSContainer extends Component, ActiveComponent, SingletonComponent {

}
