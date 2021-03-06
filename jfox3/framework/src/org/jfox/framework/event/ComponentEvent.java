/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.event;

import java.util.EventObject;

import org.jfox.framework.ComponentId;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentEvent extends EventObject {

    public ComponentEvent(ComponentId id) {
        super(id);
    }

    public ComponentId getComponentId() {
        return (ComponentId)getSource();
    }

    public static void main(String[] args) {

    }
}
