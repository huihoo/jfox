/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import org.jfox.framework.ClassLoaderRepository;

import java.net.URL;
import java.util.Arrays;

/**
 * Module ClassLoader 用来加载整个 Module 的类和资源
 * Module ClassLoader 初始化的时候，会使用ASM读取所有的Class，并记录Class的Annotation，
 * 这样可以不需要 XML 配置文件，而直接根据 Annotation 发现 Component
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class ModuleClassLoader2 extends ASMClassLoader {

    /**
     * 该 ModuleClass 负责的模块
     */
    private Module module;

    /**
     * global Class ClassLoaderRepository
     */
    private ClassLoaderRepository repo;

    /**
     * the jars in the urls can be load by Repository
     *
     * @param module module
     */
    public ModuleClassLoader2(Module module) {
        super(module.getClasspathURLs(), module.getFramework().getClassLoader());
        this.module = module;
//        this.repo = module.getFramework().getClassLoader();
        initASM();
    }

    protected URL[] getASMClasspathURLs() {
        // 返回 Module 对 classpath URLs
        return module.getClasspathURLs();
    }

    public Module getModule() {
        return module;
    }

    public ClassLoaderRepository getClassLoaderRepository() {
        return repo;
    }

    /**
     * 必须首先得找 export 列表,否则会出现重新装载,造成 ClassCastException
     *
     * @param className class className
     * @return class
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        // 如果是本模块的类，将在 findLoadedClass 中直接返回，不需要经过 ClassLoaderRepository
        Class clz = super.loadClass(className);
        if (clz.getClassLoader() instanceof ModuleClassLoader2) {
            ModuleClassLoader2 mcl = (ModuleClassLoader2)clz.getClassLoader();
            // 只有自己export 或者 ref-moudles 中 export 的class，才能返回
/*
            if ((mcl == getModule().getModuleClassLoader()) || Arrays.asList(module.getRefModules()).contains(mcl.getModule().getName())) {
                return clz;
            }
            else {
                String errorMsg = "Class: " + className + " 's export Module [" + mcl.getModule().getName() + "] is not in Module [" + getModule().getName() + "] 's ref-modules list.";
                logger.info(errorMsg);
                throw new ClassNotFoundException(errorMsg);
            }
*/
            return null;
        }
        else {
            return clz;
        }
    }

    /**
     * export class 只从本地 Module load
     *
     * @param name className
     * @return class
     * @throws ClassNotFoundException if class not foud
     */
    Class<?> loadExportClass(String name) throws ClassNotFoundException {
        logger.debug("Load export class: " + name + ", module: " + getModule().getName());
        // First, check if the class has already been loaded
        Class c = findLoadedClass(name);
        if (c == null) {
            // If still not found, then invoke findClass in order
            // to find the class.
            c = findClass(name);
        }
        return c;
    }

    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        logger.trace("Loading class: " + name + ", Module: " + getModule().getName());
        return super.findClass(name);
    }

    /**
     * 仅从本Module findResource
     *
     * @param name resource name
     */
    public URL getResource(String name) {
        return findResource(name);
    }

    public String toString() {
        return super.toString() + Arrays.toString(getURLs());
    }

}
