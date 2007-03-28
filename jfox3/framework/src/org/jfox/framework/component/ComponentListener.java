package org.jfox.framework.component;

import java.util.EventListener;

import org.jfox.framework.event.ComponentEvent;

/**
 * ComponentListener
 *
 * @see ComponentContext#fireComponentEvent(org.jfox.framework.event.ComponentEvent)
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentListener extends EventListener {

    void componentChanged(ComponentEvent componentEvent);
    
}
