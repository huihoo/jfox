package net.sourceforge.jfox.framework;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jfox.framework.event.ComponentEvent;
import net.sourceforge.jfox.framework.component.ComponentListener;
import net.sourceforge.jfox.framework.event.ModuleEvent;
import net.sourceforge.jfox.framework.component.ModuleListener;
import net.sourceforge.jfox.framework.event.FrameworkEvent;

/**
 * 事件管理器。注册/分发所有的事件
 * 
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class ListenerManager {

    private Set<FrameworkListener> frameworkListeners = new HashSet<FrameworkListener>();

    private Set<ModuleListener> moduleListeners = new HashSet<ModuleListener>();

    private Set<ComponentListener> componentListeners = new HashSet<ComponentListener>();

    public ListenerManager() {

    }

    public synchronized void registerFrameworkListener(FrameworkListener frameworkListener) {
        frameworkListeners.add(frameworkListener);
    }

    public synchronized void unregisterFrameworkListener(FrameworkListener frameworkListener) {
        frameworkListeners.remove(frameworkListener);
    }

    public synchronized void registerModuleListener(ModuleListener moduleListener) {
        moduleListeners.add(moduleListener);
    }

    public synchronized void unregisterModuleListener(ModuleListener moduleListener) {
        moduleListeners.remove(moduleListener);
    }

    public synchronized void registerComponentListener(ComponentListener componentListener) {
        componentListeners.add(componentListener);
    }

    public synchronized void unregisterComponentListener(ComponentListener componentListener) {
        componentListeners.remove(componentListener);
    }

    public synchronized void fireFrameworkEvent(FrameworkEvent frameworkEvent) {
        for(FrameworkListener listener : frameworkListeners){
            listener.frameworkEvent(frameworkEvent);
        }
    }

    public synchronized void fireModuleEvent(ModuleEvent moduleEvent) {
        for(ModuleListener listener : moduleListeners) {
            listener.moduleChanged(moduleEvent);
        }
    }

    public synchronized void fireComponentEvent(ComponentEvent componentEvent) {
        for(ComponentListener listener : componentListeners){
            listener.componentChanged(componentEvent);
        }
    }

    public static void main(String[] args) {

    }
}
