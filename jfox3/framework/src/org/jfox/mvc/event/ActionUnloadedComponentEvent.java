/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc.event;

import org.jfox.framework.ComponentId;
import org.jfox.framework.event.ComponentEvent;
import org.jfox.mvc.ActionBucket;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ActionUnloadedComponentEvent extends ComponentEvent {

    private ActionBucket actionBucket;

    public ActionUnloadedComponentEvent(ComponentId id, ActionBucket actionBucket) {
        super(id);
        this.actionBucket = actionBucket;
    }

    public ActionBucket getActionBucket() {
        return actionBucket;
    }

    public static void main(String[] args) {

    }
}