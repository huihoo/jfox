/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.component.ComponentUnregistration;
import org.jfox.framework.component.InterceptableComponent;
import org.jfox.framework.component.SingletonComponent;

/**
 *
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ActionContainer extends Component, ActiveComponent, SingletonComponent, InterceptableComponent, ComponentInitialization, ComponentUnregistration {

    PageContext invokeAction(ActionContext actionContext) throws Exception;
    
}