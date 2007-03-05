package net.sourceforge.jfox.framework;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import net.sourceforge.jfox.framework.component.Module;
import net.sourceforge.jfox.framework.component.ModuleResolvedFailedException;
import net.sourceforge.jfox.framework.component.SystemModule;
import net.sourceforge.jfox.framework.event.ModuleUnloadedEvent;
import net.sourceforge.jfox.framework.event.ModuleLoadedEvent;
import net.sourceforge.jfox.framework.event.ModuleLoadingEvent;
import net.sourceforge.jfox.util.PlaceholderUtils;
import net.sourceforge.jfox.util.FileUtils;

/**
 * JFoxNG framework.
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Framework {

    private static final Logger logger = Logger.getLogger(Framework.class);

    /**
     * ClassLoader Repository，作为Thread ContextClassLoader
     * 缓存已加载的 export class
     */
    private ClassLoaderRepository clRepo = new ClassLoaderRepository(new URL[0], Framework.class.getClassLoader());

    /**
     * 事件监听器
     */
    private ListenerManager listenerManager = new ListenerManager();

    /**
     * 系统 Module，用来加载 Framework 内注册的 Component
     */
    private SystemModule systemModule;

    /**
     * Module name => Module object
     */
    private Map<String, Module> modules = new HashMap<String, Module>();

    private boolean started = false;

    public Framework() {
        Thread.currentThread().setContextClassLoader(clRepo);
        logger.debug("Set thread context classloader to Framework ClassLoaderRepository.");

        try {
            //load global properties
            PlaceholderUtils.loadGlobalProperty(Constants.GLOBAL_PROPERTIES);
            logger.info("Loaded global placeholder config file: " + Constants.GLOBAL_PROPERTIES);
        }
        catch (IOException e) {
            logger.warn("Failed to load global placeholder properties: " + Constants.GLOBAL_PROPERTIES, e);
        }

        initSystemModule();
    }

    private void initSystemModule() {
        try {
            systemModule = new SystemModule(this);
            logger.info("System Module created.");
        }
        catch (ModuleResolvedFailedException e) {
            logger.fatal("Failed to create SystemModule!", e);
            System.exit(1);
        }
    }

    public boolean isStarted() {
        return started;
    }

    public ClassLoaderRepository getClassLoaderRepository() {
        return clRepo;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    /**
     * 获得内置的系统模块
     */
    public Module getSystemModule() {
        return systemModule;
    }

    public Module getModule(String name) {
        if (SystemModule.name.equals(name)) {
            return getSystemModule();
        }
        else {
            return modules.get(name);
        }
    }

    /**
     * 装载一个Module
     *
     *
     * @param dir Moudle所在的目录
     * @return 返回生成的 Module 实例
     */
    public Module loadModule(File dir) {
        if(!dir.exists()) {
            logger.error("Load module failed, not exists module file: " + dir);
            return null;
        }
        logger.info("Starting to load module from " + dir.getAbsolutePath());
        try {
             //.mzip 是 module 的压缩文件后缀
            if (dir.isFile() && dir.getName().endsWith(".mzip")) {
                dir = dir.getAbsoluteFile(); // 因为 getParentFile 是通过计算 path 来推断的
                File toDir = new File(dir.getParentFile(), dir.getName().substring(0, dir.getName().length() - 4));
                FileUtils.extractJar(dir, toDir);
                dir = toDir;
            }

            Module module = new Module(this, dir);
            modules.put(module.getName(), module);
            logger.info("Module: " + module.getName() + " loaded, from " + dir.getAbsolutePath());

            if (isStarted()) {// 如果 Framework 已经启动，则后续装载的 Module 立即启动
                // 发出事件
                getListenerManager().fireModuleEvent(new ModuleLoadingEvent(module));
                module.start();
                getListenerManager().fireModuleEvent(new ModuleLoadedEvent(module));
            }
            return module;
        }
        catch (Exception e) {
            logger.error("Load Module from " + dir.getAbsolutePath() + " failed.", e);
            return null;
        }
    }

    /**
     * 卸载一个模块
     *
     * @param name 模块名
     */
    public void unloadModule(String name) {
        Module module = modules.remove(name);
        if (module != null) {
            module.unload();
            getListenerManager().fireModuleEvent(new ModuleUnloadedEvent(module));
        }
    }

    /**
     * unload, then re-load
     *
     * @param name module name
     */
    public Module reloadModule(String name) {
        logger.info("Reload module: " + name);
        Module module = modules.get(name);
        if (module != null) {
            File dir = module.getModuleDir();
            unloadModule(name);
            return loadModule(dir);
        }
        return null;
    }

    /**
     * 启动所有 Module
     *
     * @throws Exception any exception
     */
    public synchronized void start() throws Exception {
        if (started) {
            logger.warn("Framework has been started, if you want restart, please stop first!");
            return;
        }
        try {
            getListenerManager().fireModuleEvent(new ModuleLoadingEvent(systemModule));
            systemModule.start();
            getListenerManager().fireModuleEvent(new ModuleLoadedEvent(systemModule));
        }
        catch (Exception e) {
            logger.fatal("Failed to start SystemModule!", e);
            System.exit(1);
        }

        List<Module> allModules = new ArrayList<Module>(modules.values());
        Collections.sort(allModules);
        for (Module module : allModules) {
            getListenerManager().fireModuleEvent(new ModuleLoadingEvent(module));
            module.start();
            getListenerManager().fireModuleEvent(new ModuleLoadedEvent(module));
        }
        started = true;
        logger.info("Framework started!");
    }

    public void stop() {
        List<Module> allModules = new ArrayList<Module>(modules.values());
        Collections.sort(allModules);
        Collections.reverse(allModules);
        for (Module module : allModules) {
            module.unload();
        }
        systemModule.unload();
        started = false;
        logger.info("Framework stopped!");
    }

    public static void main(String[] args) throws Exception {
        Framework framework = new Framework();
        framework.start();
        Thread.sleep(5000);
        framework.stop();
    }
}
