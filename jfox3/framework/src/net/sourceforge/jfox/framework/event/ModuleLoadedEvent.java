package net.sourceforge.jfox.framework.event;

import net.sourceforge.jfox.framework.component.Module;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class ModuleLoadedEvent extends ModuleEvent {

    public ModuleLoadedEvent(Module module) {
        super(module);
    }
}
