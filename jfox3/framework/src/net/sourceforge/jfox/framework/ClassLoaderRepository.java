package net.sourceforge.jfox.framework;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import net.sourceforge.jfox.util.FileFilterUtils;
import net.sourceforge.jfox.util.FileUtils;

/**
 * Class Loader Repository
 * <p/>
 * 负责管理所有Module Export 的 Class
 *
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
public class ClassLoaderRepository extends URLClassLoader {

    private Logger logger = Logger.getLogger(ClassLoaderRepository.class);

    /**
     * classname => ClassEntry
     */
    private Map<String, ClassEntry> exportClassRepo = new HashMap<String, ClassEntry>();

    public ClassLoaderRepository(URL[] urls) {
        super(urls, ClassLoaderRepository.class.getClassLoader());
        loadExternalLibs();
    }

    public ClassLoaderRepository(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        loadExternalLibs();
    }

    /**
     * 装载 common 目录下的 jar
     */
    private void loadExternalLibs() {
        File libDir = new File(Constants.getCommonLibPath());
        if (libDir.exists() && libDir.isDirectory()) {

            List<File> jarFiles = FileUtils.listFiles(libDir, FileFilterUtils.suffixFileFilter(new String[]{"jar", "zip"}));
            for (File file : jarFiles) {
                try {
                    addURL(file.toURI().toURL());
                }
                catch (MalformedURLException e) {
                    logger.warn(e);
                }
            }
        }
    }

    /**
     * 将 exported class 交给 ClassLoaderRepository 缓存
     *
     * @param module module name
     * @param clz    clz
     */
    public synchronized void addExportedClass(String module, Class clz) {
        logger.debug("Exportping Class: " + clz + ", Module: " + module);
        if (exportClassRepo.containsKey(clz.getName())) {
            logger.warn("Class: " + clz.getName() + " to be exported has been exist, overwrite!");
        }
        ClassEntry classEntry = new ClassEntry(module, clz);
        exportClassRepo.put(clz.getName(), classEntry);
    }

    /**
     * 装载 export 类
     * 在 ModuleClassLoader loadImportClass 时调用
     *
     * @param className 要装载的 class
     * @return Class
     * @throws ClassNotFoundException if class not found
     */
    Class loadExportClass(String className) throws ClassNotFoundException {
        if (className == null || className.trim().length() == 0) {
            throw new IllegalArgumentException("Class name: " + className + " is invalid.");
        }
        if (exportClassRepo.containsKey(className)) {
            ClassEntry classEntry = exportClassRepo.get(className);
            //只有被外部模块饮用才会增加引用次数,如果是被本模块load,ModuleClassLoader会调用decreaseReference
            //如果是本模块的类，将在 findLoadedClass 中返回，不会经过 ClassLoaderRepository
            classEntry.setReferenced();
            return classEntry.getClazz();
        }
        throw new ClassNotFoundException("Can not found class: " + className + ", it was not exported.");
    }

    boolean isExportClassLoaded(String className) {
        return exportClassRepo.containsKey(className);
    }


    /**
     * 判断一个 Export Class 是否已经被引用,如果已经被引用,将不能销毁
     * 如果一定要销毁,那么也需要引用它的类
     *
     * @param clz Class
     * @return true if referenced
     */
    boolean isExportedClassReferenced(Class clz) {
        if (exportClassRepo.containsKey(clz.getName())) {
            return exportClassRepo.get(clz.getName()).isReferenced();
        }
        else {
            return false;
        }
    }

    /**
     * 添加 URL 到 Classpath 搜索路径
     *
     * @param url jar url
     */
    protected void addURL(URL url) {
        logger.debug("Add URL " + url.toString() + " to ClassLoaderRepository.");
        super.addURL(url);
    }

    /**
     * 因为 ClassLoaderRepository 是 LocalClassLoader 的 parent ClassLoader
     * LocalClassLoader 会直接调这个方法，所以必须要覆盖
     * 首先从 ClassLoaderRepository 的classpath路径中装载类，如果失败， 装载缓存的 LocalClassLoader
     * 已经装载的类
     *
     * @param name    class name
     * @param resolve need resole
     * @return class
     * @throws ClassNotFoundException
     */
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException e) {
            try {
                Class clz = loadExportClass(name);
                if (resolve) {
                    resolveClass(clz);
                }
                return clz;
            }
            catch (ClassNotFoundException e1) {
//                e.printStackTrace();
            }
        }
        throw new ClassNotFoundException(name);
    }

    /**
     * 一个Module export 的类，这些类一旦装载，热部署的时候，也不会重新装载
     * 这种类一旦装载，就被缓存在Repository中，以后load的时候，将首先检查缓存
     * <p/>
     * export的类一般是一些接口或者工具类。
     * 如果export的类发生的改变，那么就需要重新部署已经引用了export类的模块，
     * 否则引用模块得不到新的Class，会造成 ClassCastException 错误
     */
    class ClassEntry {
        private String module;
        private Class clz;
        private boolean referenced = false;

        public ClassEntry(String module, Class clz) {
            this.module = module;
            this.clz = clz;
        }

        public Class getClazz() {
            return clz;
        }

        public String getModule() {
            return module;
        }

        public boolean isReferenced() {
            return referenced;
        }

        public void setReferenced() {
            this.referenced = true;
        }
    }

    public static void main(String[] args) {

    }
}