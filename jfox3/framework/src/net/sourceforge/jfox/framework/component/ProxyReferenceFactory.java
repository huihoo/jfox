package net.sourceforge.jfox.framework.component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Enumeration;

import net.sourceforge.jfox.framework.ClassLoaderRepository;
import net.sourceforge.jfox.framework.ComponentId;
import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.framework.invoker.ComponentInvoker;

/**
 * 为 Component 生成基于动态代理的弱引用
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
class ProxyReferenceFactory {

    /**
     * 创建 Component 的动态代理对象，在对象引用的时候，传递的都是该对象
     *
     * 传递到引用的好处就是，在Component重新部署的时候，
     * 只要部署的ComponentId没变，依然可以调用，这样就可以支持按模块的部署了
     *
     * @param framework framework
     * @param module module
     * @param componentId componentId
     * @param interfaces component interface list
     * @param componentInvoker 调用器，由ComponentInvokerFactory根据 Component Type 选择
     * @return dynamic proxy component reference
     */
    public static Component createProxyComponent(
            final Framework framework,
            final String module,
            final ComponentId componentId,
            final Class[] interfaces,
            final ComponentInvoker componentInvoker) {
        // 返回动态代理对象
        return (Component)Proxy.newProxyInstance(
                // 实现动态的Classloader，以便在 Module reload 时，可以使用新的 ClassLoader
                new DelegateModuleClassLoader(framework,module),
                interfaces,
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Module moduleObj = framework.getModule(module);
                        ComponentMeta componentMeta = moduleObj.getComponentMeta(componentId);
                        Component component = componentMeta.getConcreteComponent();
                        if(args == null && method.getName().equals("toString")) {
                            return "$proxy:" + componentId.toString();
                        }
                        //TODO: get ImplementationClass 的 Method，以便分析其 annotation 
                        return componentInvoker.invokeMethod(component, componentId, method, args);
                    }
                }
        );
    }


    static class DelegateModuleClassLoader extends ClassLoader {
        private Framework framework;
        private String module;

        public DelegateModuleClassLoader(Framework framework, String module) {
            this.framework = framework;
            this.module = module;
        }

        private ModuleClassLoader getModuleClassLoader(){
            return framework.getModule(module).getModuleClassLoader();
        }


        public void clearAssertionStatus() {
            getModuleClassLoader().clearAssertionStatus();
        }


        public Class[] findClassAnnotatedWith(Class<? extends Annotation> annotation) {
            return getModuleClassLoader().findClassAnnotatedWith(annotation);
        }

        public URL findResource(String name) {
            return getModuleClassLoader().findResource(name);
        }

        public Enumeration<URL> findResources(String name) throws IOException {
            return getModuleClassLoader().findResources(name);
        }

        public ClassLoaderRepository getClassLoaderRepository() {
            return getModuleClassLoader().getClassLoaderRepository();
        }

        public Module getModule() {
            return getModuleClassLoader().getModule();
        }

        public URL getResource(String name) {
            return getModuleClassLoader().getResource(name);
        }

        public InputStream getResourceAsStream(String name) {
            return getModuleClassLoader().getResourceAsStream(name);
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            return getModuleClassLoader().getResources(name);
        }


        public URL[] getURLs() {
            return getModuleClassLoader().getURLs();
        }

        public Class<?> loadClass(String className) throws ClassNotFoundException {
            return getModuleClassLoader().loadClass(className);
        }

        public void setClassAssertionStatus(String className, boolean enabled) {
            getModuleClassLoader().setClassAssertionStatus(className, enabled);
        }

        public void setDefaultAssertionStatus(boolean enabled) {
            getModuleClassLoader().setDefaultAssertionStatus(enabled);
        }

        public void setPackageAssertionStatus(String packageName, boolean enabled) {
            getModuleClassLoader().setPackageAssertionStatus(packageName, enabled);
        }

        public String toString() {
            return getModuleClassLoader().toString();
        }
    }

    public static void main(String[] args) {

    }
}
