package net.sourceforge.jfox.framework;

import net.sourceforge.jfox.framework.event.FrameworkEvent;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface FrameworkListener {

    void frameworkEvent(FrameworkEvent registryEvent);
    
}
