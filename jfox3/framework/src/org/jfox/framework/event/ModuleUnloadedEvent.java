package org.jfox.framework.event;

import org.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ModuleUnloadedEvent extends ModuleEvent {

    public ModuleUnloadedEvent(Module module) {
        super(module);
    }

}
