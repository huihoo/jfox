/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.classloader;

import java.net.URL;
import java.util.Arrays;

/**
 * Module ClassLoader 用来加载整个 Module 的类和资源
 * Module ClassLoader 初始化的时候，会使用ASM读取所有的Class，并记录Class的Annotation，
 * 这样可以不需要 XML 配置文件，而直接根据 Annotation 发现 Component
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class ModuleClassLoader extends ASMClassLoader {

    public ModuleClassLoader(URL[] classpathURLs, FrameworkClassLoader frameworkClassLoader) {
        super(frameworkClassLoader);
        parseModuleClasses(classpathURLs);
    }

    public String toString() {
        return super.toString() + Arrays.toString(getURLs());
    }

}