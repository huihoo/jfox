/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.classloader;

import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * 作为整个框架的 ClassLoader，该 Classloader 并不具备类加载功能，所有的功能都委派给其 parent ClassLoader，一般即WebAppClassloader
 * 该类做两件事：
 * 1. 使用 asm 分析类路径中的类的 Annotation
 * 2. public addURL 
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FrameworkClassLoader extends ASMClassLoader {

    public FrameworkClassLoader(URLClassLoader sysClassLoader) {
        super(sysClassLoader);
        parseModuleClasses(sysClassLoader.getURLs());
    }

    public String toString() {
        return super.toString() + Arrays.toString(getURLs());
    }
}