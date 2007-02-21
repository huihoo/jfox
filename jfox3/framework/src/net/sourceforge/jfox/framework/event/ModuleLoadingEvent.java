package net.sourceforge.jfox.framework.event;

import net.sourceforge.jfox.framework.component.Module;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class ModuleLoadingEvent extends ModuleEvent {

    public ModuleLoadingEvent(Module module) {
        super(module);
    }
}
