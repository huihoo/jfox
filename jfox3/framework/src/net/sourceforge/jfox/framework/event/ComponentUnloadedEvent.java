package net.sourceforge.jfox.framework.event;

import net.sourceforge.jfox.framework.ComponentId;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class ComponentUnloadedEvent extends ComponentEvent{

    public ComponentUnloadedEvent(ComponentId id) {
        super(id);
    }
}
