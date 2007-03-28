package org.jfox.framework.event;

import org.jfox.framework.ComponentId;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentLoadedEvent extends ComponentEvent{

    public ComponentLoadedEvent(ComponentId id) {
        super(id);
    }
}
