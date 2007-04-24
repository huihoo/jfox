package org.jfox.framework.component;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

import org.jfox.ejb3.EJBContainer;
import org.jfox.framework.Framework;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.event.ModuleLoadedEvent;
import org.jfox.framework.event.ModuleLoadingEvent;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SystemModule extends Module {

    public static final String name = "__SYSTEM_MODULE__";

    public SystemModule(Framework framework) throws ModuleResolvedFailedException {
        super(framework, null);
    }

    protected ModuleClassLoader initModuleClassLoader() {
        // 覆盖 getResource，以便能够正确检索到 resource
        return new ModuleClassLoader(this) {
            public URL getResource(String name) {
                // parent 是 ClassLoaderRepository
                return getParent().getResource(name);
            }

            protected URL[] getASMClasspathURLs() {
                return super.getASMClasspathURLs();
            }
        };
    }

    protected void resolve() throws ModuleResolvedFailedException {
        setName(name);
        setDescription("System Module");
        setPriority(Integer.MIN_VALUE);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return super.getDescription();
    }

    /**
     * SystemModule的 classpath 已经指定在启动 classpath中
     */
    public URL[] getClasspathURLs() {
        return ((URLClassLoader)SystemModule.class.getClassLoader()).getURLs();
    }

    public URL getDescriptorURL() {
        return null;
    }
    /**
     * 解析模块，export class, 装载组件
     *
     * @throws Exception any exception
     */
    public void start() throws Exception {
        logger.info("Starting module: " + getName());
        Class[] deployComponents = getModuleClassLoader().findClassAnnotatedWith(Service.class);
        for (Class<?> componentClass : deployComponents) {
            if (componentClass.isInterface()
                    || !Modifier.isPublic(componentClass.getModifiers())
                    || Modifier.isAbstract(componentClass.getModifiers())) {
                logger.warn("Class " + componentClass.getName() + " is annotated with @" + Service.class.getSimpleName() + ", but not is not a public concrete class, ignored!");
                continue;
            }
            if (!Component.class.isAssignableFrom(componentClass)) {
                logger.warn("Class " + componentClass.getName() + " is annotated with @" + Service.class.getSimpleName() + ", but not implements interface " + Component.class.getName() + ", ignored!");
                continue;
            }
            ComponentMeta meta = loadComponent(componentClass.asSubclass(Component.class));
            logger.info("Component " + componentClass.getName() + " loaded with id: " + meta.getComponentId() + "!");
        }

        // will instantiate EJBContainer
        findComponentByInterface(EJBContainer.class);
        // then fire ModuleLoadingEvent, so EJB Container can load EJB in SYSTEM_MODULE
        getFramework().getEventManager().fireModuleEvent(new ModuleLoadingEvent(this));
        
        // 实例化 not lazy components
        instantiateActiveComponent();
        getFramework().getEventManager().fireModuleEvent(new ModuleLoadedEvent(this));
    }

    public static void main(String[] args) {

    }
}
