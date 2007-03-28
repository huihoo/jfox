package org.jfox.framework.component;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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
     * SystemModule的 classpath 已经制定在启动 classpath中，无须再添加
     */
    public URL[] getClasspathURLs() {
        URL[] urls = ((URLClassLoader)SystemModule.class.getClassLoader()).getURLs();
        // 只返回含有 Component 类的路径

        List<URL> appURLs = new ArrayList<URL>();
        for (URL url : urls) {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, null);
            URL testURL = urlClassLoader.findResource(Object.class.getName().replace(".", "/") + ".class");
            //滤掉 rt.jar
            //TODO: 滤掉更多的 jdk jar
            if (testURL == null) {
                appURLs.add(url);
            }

        }
        return appURLs.toArray(new URL[appURLs.size()]);
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
