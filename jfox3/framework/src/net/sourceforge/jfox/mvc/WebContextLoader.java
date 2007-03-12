package net.sourceforge.jfox.mvc;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.util.FileFilterUtils;
import org.apache.log4j.Logger;

/**
 * Web Context Loader，initialize framework when jfox3 web application loaded
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class WebContextLoader implements ServletContextListener {

    public static final String MODULES_DIR = "MODULES_DIR";

    private static Logger logger = Logger.getLogger(WebContextLoader.class);

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
        //记录启动消耗时间
        long now = System.currentTimeMillis();
        framework = new Framework();
        try {
            String _modulesDir = servletContextEvent.getServletContext().getInitParameter(MODULES_DIR);
            if(_modulesDir == null || _modulesDir.trim().length() == 0){
                logger.warn("MODULES_DIR not configured in web.xml!");
                return;
            }

            if(!_modulesDir.startsWith("/")) {
                // forward url必须以 / 开头 
                _modulesDir = "/" + _modulesDir;
            }

            if(_modulesDir == null) {
                logger.warn("No modules dir configed to deploy!");
                return;
            }

            File modulesDir = new File(servletContextEvent.getServletContext().getRealPath("/"), _modulesDir);
            if(!modulesDir.exists()){
                logger.warn("Modules dir configured in web.xml not exists, " + modulesDir.toString());
                return;
            }

            File[] moduleDirs = modulesDir.listFiles(FileFilterUtils.directoryFileFilter());
            for(File moduleDir : moduleDirs){
                framework.loadModule(moduleDir);
                // register module path
                registerModulePath(_modulesDir + "/" + moduleDir.getName(), moduleDir);
            }
            framework.start();
        }
        catch(Exception e) {
            logger.error("Start framework failed!", e);
        }
        logger.info("JFox started in " + ((System.currentTimeMillis()-now)/1000) + " seconds!");
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
