package org.jfox.framework.event;

import org.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ModuleLoadedEvent extends ModuleEvent {

    public ModuleLoadedEvent(Module module) {
        super(module);
    }
}
