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
import org.jfox.mvc.ActionContainer;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SystemModule extends Module {

    public static final String name = "__SYSTEM_MODULE__";
    URL[] classpathURLs = null;

    public SystemModule(Framework framework) throws ModuleResolvedFailedException {
        super(framework, null);
        // 新的模块加入时，当前ClassLoader的 getURLs会变化，所以一加载的时候就保存下来
        classpathURLs = ((URLClassLoader)SystemModule.class.getClassLoader()).getURLs();
    }

    public String getName() {
        return name;
    }

    /**
     * SystemModule的 classpath 已经指定在启动 classpath中
     */
    public URL[] getClasspathURLs() {
        // System Module have same classpath with FrameworkClassLoader
        return classpathURLs;
    }

    protected boolean isSystemModule(){
        return true;
    }

    // 在 fire ModuleLoadingEvent之前加载以下组件，以便能监听ModuleLoadingEvent
    protected void preActiveComponent() {
        // instantiate EJB container
        findComponentsByInterface(EJBContainer.class);
        // instantiate JPA container
        findComponentsByInterface(EntityManagerFactoryBuilder.class);
        // instantiate Web Service container
        findComponentsByInterface(ContainerInvoker.class);

        // instantiate MVC Action Container
        findComponentsByInterface(ActionContainer.class);
    }

    public static void main(String[] args) {

    }
}
