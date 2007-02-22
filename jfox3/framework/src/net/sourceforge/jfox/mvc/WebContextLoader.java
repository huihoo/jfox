package net.sourceforge.jfox.mvc;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.framework.component.Module;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class WebContextLoader implements ServletContextListener {

    public static final String MODULES = "MODULES";

    private Framework framework = null;

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
        framework = new Framework();
        try {
            String toDeployModules = servletContextEvent.getServletContext().getInitParameter(MODULES);
            String[] modulePaths = toDeployModules.split(",");
            for(String modulePath : modulePaths){
                File moduleDir = new File(servletContextEvent.getServletContext().getRealPath("/"), modulePath);
                Module module = framework.loadModule(moduleDir);
                // register module path
                registerModulePath(modulePath, moduleDir);
            }
            framework.start();
        }
        catch(Exception e) {
            servletContextEvent.getServletContext().log("Start framework failed!", e);
        }

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            if(framework != null) {
                framework.stop();
            }
        }
        catch(Exception e) {
            servletContextEvent.getServletContext().log("Stop framework failed!", e);
        }
    }

    /**
     * 注册模块目录名到 模块路径的映射
     *
     * @param moduleDirPath module path
     * @param moduleDir module real dir
     */
    public static void registerModulePath(String moduleDirPath, File moduleDir) {
        modulePath2File.put(moduleDirPath,moduleDir);

        String moduleDirName = moduleDirPath.substring(moduleDirPath.lastIndexOf("/") + 1);
        moduleDirName2PathMap.put(moduleDirName, moduleDirPath);
    }

    public static Map<String, File> getModulePath2FileMap() {
        return Collections.unmodifiableMap(modulePath2File);
    }

    public static void registerAction(String moduleDirName, ActionSupport action) {
        if (!module2ActionsMap.containsKey(moduleDirName)) {
            module2ActionsMap.put(moduleDirName, new HashMap<String, ActionSupport>());
        }
        Map<String, ActionSupport> actionMap = module2ActionsMap.get(moduleDirName);
        actionMap.put(action.getName(), action);
    }

    public static Action removeAction(Action action) {
        //不是 ModuleName，而是 Module Dir name
        String module = ((ActionSupport)action).getModuleDirName();
        if (module2ActionsMap.containsKey(module)) {
            return module2ActionsMap.get(module).remove(((ActionSupport)action).getName());
        }
        return null;
    }

    public static String getModulePathByModuleDirName(String moduleDirName){
        return moduleDirName2PathMap.get(moduleDirName);
    }

    public static Action getAction(String moduleDirName, String actionName){
        return module2ActionsMap.get(moduleDirName).get(actionName);
    }

    public static void main(String[] args) {

    }
}
