package org.jfox.mvc;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jfox.framework.Framework;
import org.jfox.framework.component.Module;
import org.jfox.util.FileFilterUtils;
import org.apache.log4j.Logger;

/**
 * Web Context Loader，initialize framework when jfox3 web application loaded
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class WebContextLoader implements ServletContextListener {

    public static final String MODULES_DIR = "MODULES_DIR";

    private static Logger logger = Logger.getLogger(WebContextLoader.class);

    private static Framework framework = null;

    /**
     * Module Dir Name => Module Path
     */
    private static Map<String, String> moduleDirName2PathMap = new HashMap<String, String>();

    /**
     * 缓存所有的 Action
     * module dir name => {Actoin deploy id => action}
     */
    private static Map<String, Map<String, ActionSupport>> module2ActionsMap = new ConcurrentHashMap<String, Map<String, ActionSupport>>();

    /**
     * module relative path => module real dir File
     */
    private static Map<String, File> modulePath2File = new HashMap<String, File>();

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //记录启动消耗时间
        long now = System.currentTimeMillis();
        framework = new Framework();

        try {
            String _modulesDir = servletContextEvent.getServletContext().getInitParameter(MODULES_DIR);
            if (_modulesDir == null || _modulesDir.trim().length() == 0) {
                logger.warn("MODULES_DIR not configured in web.xml!");
            }
            else {
                if (!_modulesDir.startsWith("/")) {
                    // forward url必须以 / 开头，否则 ControllerServlet forward 出错
                    _modulesDir = "/" + _modulesDir;
                }

                File modulesDir = new File(servletContextEvent.getServletContext().getRealPath("/"), _modulesDir);
                if (!modulesDir.exists()) {
                    logger.warn("Modules dir configured in web.xml not exists, " + modulesDir.toString());
                    return;
                }

                // 过滤掉 . 开头的目录

                // zip 文件
                File[] moduleZips = modulesDir.listFiles(FileFilterUtils.and(FileFilterUtils.not(FileFilterUtils.directoryFileFilter()),FileFilterUtils.suffixFileFilter(Framework.MODULE_ARCHIVE_SUFFIX), FileFilterUtils.not(FileFilterUtils.prefixFileFilter("."))));
                for(File moduleZip : moduleZips){
                    Module module = framework.loadModule(moduleZip);
                    registerModulePath(_modulesDir + "/" + moduleZip.getName().substring(0, moduleZip.getName().lastIndexOf(".")), module.getModuleDir());
                }

                // module 目录
                File[] moduleDirs = modulesDir.listFiles(FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.not(FileFilterUtils.prefixFileFilter("."))));
                for (File moduleDir : moduleDirs) {
                    framework.loadModule(moduleDir);
                    // register module path
                    registerModulePath(_modulesDir + "/" + moduleDir.getName(), moduleDir);
                }
            }
            // start framework, will start all modules
            framework.start();
            logger.info("JFox started in " + ((System.currentTimeMillis() - now) / 1000) + " seconds!");
        }
        catch (Exception e) {
            logger.error("Start framework failed!", e);
        }
    }

    /**
     * 返回 framework, 供Web management console用
     */
    public static Framework getManagedFramework() {
        return framework;
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //记录停止消耗时间
        long now = System.currentTimeMillis();
        try {
            if (framework != null) {
                framework.stop();
                // 清除资源，以便能正常回收，否则webapp 目录可能无法被删除
                moduleDirName2PathMap.clear();
                module2ActionsMap.clear();
                modulePath2File.clear();
                framework = null;
                System.gc();
                Thread.sleep(2000);
                logger.info("JFox stopped in " + ((System.currentTimeMillis() - now) / 1000) + " seconds!");
            }
        }
        catch (Exception e) {
            servletContextEvent.getServletContext().log("Stop framework failed!", e);
        }
    }

    /**
     * 注册模块目录名到 模块路径的映射
     *
     * @param moduleDirPath module path
     * @param moduleDir     module real dir
     */
    private static void registerModulePath(String moduleDirPath, File moduleDir) {
        modulePath2File.put(moduleDirPath, moduleDir);

        String moduleDirName = moduleDirPath.substring(moduleDirPath.lastIndexOf("/") + 1);
        moduleDirName2PathMap.put(moduleDirName, moduleDirPath);
    }

    /**
     * 得到所有模块路径名到模块目录的映射，如: WEB-INF/MODULES/manager => File://D:/%TOMCAT_HOME%/webapps/jfox3/WEB-INF/MODULES/manager
     */
    public static Map<String, File> getModulePath2DirFileMap() {
        return Collections.unmodifiableMap(modulePath2File);
    }

    /**
     * 通过模块目录名如:manager 得到模块的路径如：WEB-INF/MODULES/manager
     * @param moduleDirName 模块目录名
     */
    public static String getModulePathByModuleDirName(String moduleDirName) {
        return moduleDirName2PathMap.get(moduleDirName);
    }

    /**
     * 注册 Action，由 ActionSupport 在 postPropertiesSet 中调用
     * @param moduleDirName 模块目录名
     * @param action Action 实例
     */
    public static void registerAction(String moduleDirName, ActionSupport action) {
        if (!module2ActionsMap.containsKey(moduleDirName)) {
            module2ActionsMap.put(moduleDirName, new HashMap<String, ActionSupport>());
        }
        Map<String, ActionSupport> actionMap = module2ActionsMap.get(moduleDirName);
        actionMap.put(action.getName(), action);
    }

    /**
     * 删除 Action，在 Action unrigister的时候会调用该方法，有 module unload触发
     * @param action action实例
     */
    public static Action removeAction(Action action) {
        //不是 ModuleName，而是 Module Dir name
        String module = ((ActionSupport)action).getModuleDirName();
        if (module2ActionsMap.containsKey(module)) {
            return module2ActionsMap.get(module).remove(((ActionSupport)action).getName());
        }
        return null;
    }

    private static Action getAction(String moduleDirName, String actionName) {
        return module2ActionsMap.get(moduleDirName).get(actionName);
    }

    public static void invokeAction(String moduleDirName, String actionName, InvocationContext invocationContext) throws Exception {
        Action action = WebContextLoader.getAction(moduleDirName, actionName);
        if(action == null) {
            throw new ActionNotFoundException("Can not found Action: " + action + " in Module: " + moduleDirName);
        }
        action.execute(invocationContext);
    }

    public static void main(String[] args) {

    }
}
