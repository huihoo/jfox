/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework;

import org.apache.log4j.Logger;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInstantiateException;
import org.jfox.framework.component.ComponentMeta;
import org.jfox.framework.component.ComponentNotExportedException;
import org.jfox.framework.component.ComponentNotFoundException;
import org.jfox.framework.component.Module;
import org.jfox.framework.component.ModuleResolvedFailedException;
import org.jfox.framework.component.Repository;
import org.jfox.framework.component.SystemModule;
import org.jfox.framework.event.FrameworkStartedEvent;
import org.jfox.framework.event.FrameworkStoppedEvent;
import org.jfox.util.FileUtils;
import org.jfox.util.PlaceholderUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JFoxNG framework.
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Framework {

    private static final Logger logger = Logger.getLogger(Framework.class);


    private URLClassLoader frameworkClassLoader;

    /**
     * 事件监听器
     */
    private EventManager listenerManager = new EventManager();

    /**
     * 系统 Module，用来加载 Framework 内注册的 Component
     */
    private SystemModule systemModule;

    /**
     * Module name => Module object
     */
    private Map<String, Module> modules = new HashMap<String, Module>();

    private boolean started = false;

    public static final String MODULE_ARCHIVE_SUFFIX = ".zip";

    public final static Repository repository = Repository.getInstance();

    public Framework() {
        frameworkClassLoader = new FrameworkClassLoader((URLClassLoader)Framework.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(frameworkClassLoader);

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

    public URLClassLoader getClassLoader() {
        return frameworkClassLoader;
    }

    public EventManager getEventManager() {
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
             //.zip 是 module 的压缩文件后缀
            if (dir.isFile() && dir.getName().endsWith(MODULE_ARCHIVE_SUFFIX)) {
                dir = dir.getAbsoluteFile(); // 因为 getParentFile 是通过计算 path 来推断的
                File toDir = new File(dir.getParentFile(), dir.getName().substring(0, dir.getName().length() - MODULE_ARCHIVE_SUFFIX.length()));
                FileUtils.extractJar(dir, toDir);
                dir = toDir;
            }

            Module module = new Module(this, dir);
            modules.put(module.getName(), module);
            logger.info("Module: " + module.getName() + " loaded, from " + dir.getAbsolutePath());

            if (isStarted()) {// 如果 Framework 已经启动，则后续装载的 Module 立即启动
                module.init();
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
    public void unloadModule(String name) throws Exception{
        Module module = modules.remove(name);
        if (module != null) {
            module.destroy();
        }
    }

    /**
     * unload, then re-load
     *
     * @param name module name
     */
    public Module reloadModule(String name) throws Exception {
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
            systemModule.init();
        }
        catch (Exception e) {
            logger.fatal("Failed to start SystemModule!", e);
            System.exit(1);
        }

        List<Module> allModules = new ArrayList<Module>(modules.values());
        Collections.sort(allModules, new Comparator<Module>(){
            public int compare(Module m1, Module m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });
        for (Module module : allModules) {
            module.init();
        }
        started = true;
        getEventManager().fireFrameworkEvent(new FrameworkStartedEvent(this));
        logger.info("Framework started!");
    }

    public void stop() throws Exception{
        List<Module> allModules = new ArrayList<Module>(getAllModules());
        Collections.reverse(allModules);
        for (Module module : allModules) {
            module.destroy();
        }
        systemModule.destroy();
        started = false;
        getEventManager().fireFrameworkEvent(new FrameworkStoppedEvent(this));
        logger.info("Framework stopped!");
    }

    public List<Module> getAllModules(){
        List<Module> allModules = new ArrayList<Module>(modules.values());
        Collections.sort(allModules, new Comparator<Module>(){
            public int compare(Module m1, Module m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });
        Collections.reverse(allModules);
        return Collections.unmodifiableList(allModules);
    }

    public List<ComponentMeta> getComponentMetas(){
        return repository.getComponentMetas();
    }

    public ComponentMeta getComponentMeta(ComponentId id) throws ComponentNotFoundException {
        return repository.getComponentMeta(id);
    }

    /**
     * 获得该模块内的 Component 实例
     *
     * @param componentId componentId
     * @throws ComponentNotFoundException    if not found the component or component instantiate failed
     * @throws ComponentNotExportedException if found component in other module, but it is not exported
     */
    public Component getComponent(ComponentId componentId) throws ComponentNotFoundException, ComponentInstantiateException {
        return getComponentMeta(componentId).getComponentInstance();
    }

    public Component getComponent(String componentId) throws ComponentNotFoundException, ComponentInstantiateException {
        return getComponent(new ComponentId(componentId));
    }

    public boolean isComponentLoaded(ComponentId id) {
        return repository.hasComponentMeta(id);
    }


    public static void main(String[] args) throws Exception {
        Framework framework = new Framework();
        framework.start();
        Thread.sleep(5000);
        framework.stop();
    }
}
