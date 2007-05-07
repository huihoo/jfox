/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.event;

import java.util.EventListener;

import org.jfox.framework.event.ComponentEvent;

/**
 * ComponentListener
 *
 * @see org.jfox.framework.component.ComponentContext#fireComponentEvent(org.jfox.framework.event.ComponentEvent)
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentListener extends EventListener {

    void componentChanged(ComponentEvent componentEvent);
    
}
