package net.sourceforge.jfox.framework.component;

import java.util.EventListener;

import net.sourceforge.jfox.framework.event.ComponentEvent;

/**
 * ComponentListener
 *
 * @see ComponentContext#fireComponentEvent(net.sourceforge.jfox.framework.event.ComponentEvent) 
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentListener extends EventListener {

    void componentChanged(ComponentEvent componentEvent);
    
}
