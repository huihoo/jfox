package net.sourceforge.jfox.ejb3;

import net.sourceforge.jfox.framework.event.ComponentEvent;
import net.sourceforge.jfox.framework.ComponentId;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class EJBUnloadedComponentEvent extends ComponentEvent {

    public EJBUnloadedComponentEvent(ComponentId id) {
        super(id);
    }

    public static void main(String[] args) {

    }
}
