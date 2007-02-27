package net.sourceforge.jfox.ejb3.event;

import net.sourceforge.jfox.framework.event.ComponentEvent;
import net.sourceforge.jfox.framework.ComponentId;
import net.sourceforge.jfox.ejb3.EJBBucket;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class EJBUnloadedComponentEvent extends ComponentEvent {

    private EJBBucket ejbBucket;

    public EJBUnloadedComponentEvent(ComponentId id, EJBBucket ejbBucket) {
        super(id);
        this.ejbBucket = ejbBucket;
    }

    public EJBBucket getEJBBucket() {
        return ejbBucket;
    }

    public static void main(String[] args) {

    }
}
