package org.jfox.framework.component;

import org.jfox.framework.event.ModuleEvent;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ModuleListener {

    void moduleChanged(ModuleEvent moduleEvent);

}
