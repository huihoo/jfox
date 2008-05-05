/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import org.jfox.ejb3.EJBContainer;
import org.jfox.ejb3.remote.ContainerInvoker;
import org.jfox.entity.EntityManagerFactoryBuilder;
import org.jfox.framework.Framework;

import java.net.URL;
import java.net.URLClassLoader;

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

    // 在 fire ModuleLoadingEvent之前加载以下组件，以便能监听ModuleLoadingEvent
    protected void preActiveComponent() {
        // instantiate EJB container
        findComponentByInterface(EJBContainer.class);
        // instantiate JPA container
        findComponentByInterface(EntityManagerFactoryBuilder.class);
        // instantiate Web Service container
        findComponentByInterface(ContainerInvoker.class);
    }

    public static void main(String[] args) {

    }
}
