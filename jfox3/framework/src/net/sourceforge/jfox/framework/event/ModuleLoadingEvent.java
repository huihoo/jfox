package net.sourceforge.jfox.framework.event;

import net.sourceforge.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ModuleLoadingEvent extends ModuleEvent {

    public ModuleLoadingEvent(Module module) {
        super(module);
    }
}
