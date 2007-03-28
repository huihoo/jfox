package org.jfox.framework;

import org.jfox.framework.event.FrameworkEvent;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface FrameworkListener {

    void frameworkEvent(FrameworkEvent registryEvent);
    
}
