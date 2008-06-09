/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import org.apache.log4j.Logger;
import org.jfox.framework.ComponentId;
import org.jfox.framework.Framework;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.Module;
import org.jfox.util.FileFilterUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
     * Module Dir Name => Module Path, 如：manager=>/WEB-INF/MODULES/ccmis
     */
    private static Map<String, String> moduleDirName2PathMap = new HashMap<String, String>();

    // manager=>JFox Management
    private static Map<String, String> moduleDirName2ModuleName = new HashMap<String, String>();

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //记录启动消耗时间
        long now = System.currentTimeMillis();
        framework = new Framework();

        try {
            File modulesDir = null;
            String _modulesDir = servletContextEvent.getServletContext().getInitParameter(MODULES_DIR);
            if (_modulesDir == null || _modulesDir.trim().length() == 0) {
                logger.warn("MODULES_DIR not configured in web.xml!");
            }
            else {
                if (!_modulesDir.startsWith("/")) {
                    // forward url必须以 / 开头，否则 ControllerServlet forward 出错
                    _modulesDir = "/" + _modulesDir;
                    modulesDir = new File(servletContextEvent.getServletContext().getRealPath("/"), _modulesDir);
                }
                else {
                    // 支持绝对路径
                    modulesDir = new File(_modulesDir);
                }
                
                if (!modulesDir.exists()) {
                    logger.warn("Modules dir configured in web.xml not exists, " + modulesDir.toString());
                    return;
                }

                // 过滤掉 . 开头的目录

                // zip 文件
                File[] moduleZips = modulesDir.listFiles(FileFilterUtils.and(FileFilterUtils.not(FileFilterUtils.directoryFileFilter()),FileFilterUtils.suffixFileFilter(Framework.MODULE_ARCHIVE_SUFFIX), FileFilterUtils.not(FileFilterUtils.prefixFileFilter("."))));
                for(File moduleZip : moduleZips){
                    Module module = framework.loadModule(moduleZip);
                    registerModulePath(_modulesDir + "/" + module.getModuleDir().getName());
                    moduleDirName2ModuleName.put(module.getModuleDir().getName(), module.getName());
                }

                // module 目录
                File[] moduleDirs = modulesDir.listFiles(FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.not(FileFilterUtils.prefixFileFilter("."))));
                for (File moduleDir : moduleDirs) {
                    Module module = framework.loadModule(moduleDir);
                    // register module path
                    registerModulePath(_modulesDir + "/" + moduleDir.getName());
                    moduleDirName2ModuleName.put(moduleDir.getName(), module.getName());
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
                moduleDirName2ModuleName.clear();
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
     */
    private static void registerModulePath(String moduleDirPath) {
        String moduleDirName = moduleDirPath.substring(moduleDirPath.lastIndexOf("/") + 1);
        moduleDirName2PathMap.put(moduleDirName, moduleDirPath);
    }

    /**
     * 得到所有模块路径名到模块目录的映射，如: WEB-INF/MODULES/manager => File://D:/%TOMCAT_HOME%/webapps/jfox3/WEB-INF/MODULES/manager
     * 供 Velocity 和 FreeMarker 按URL路径匹配模块模板文件
     */
/*
    public static Map<String, File> getModulePath2DirFileMap() {
        return Collections.unmodifiableMap(modulePath2File);
    }
*/

    public static String[] getModuleDirNames(){
        return moduleDirName2PathMap.keySet().toArray(new String[moduleDirName2PathMap.size()]);
    }

    public static boolean isModuleExists(String moduleDirName){
        if(!moduleDirName2ModuleName.containsKey(moduleDirName)){
            return false;
        }
        if(framework.getModule(moduleDirName2ModuleName.get(moduleDirName)) == null){
            return false;
        }
        return true;
    }

    /**
     * 通过模块目录名如:manager 得到模块的路径如：WEB-INF/MODULES/manager
     * @param moduleDirName 模块目录名
     */
    public static String getModulePathByModuleDirName(String moduleDirName) {
        return moduleDirName2PathMap.get(moduleDirName);
    }

    public static File getModuleDirByModuleDirName(String moduleDirName) {
        if(!isModuleExists(moduleDirName)) {
            throw new ModuleNotExistedException(moduleDirName);
        }
        Module module = framework.getModule(moduleDirName2ModuleName.get(moduleDirName));
        return module.getModuleDir();
    }


    private static Action getAction(String moduleDirName, String actionName) throws Exception {
        if(!isModuleExists(moduleDirName)) {
            throw new ModuleNotExistedException(moduleDirName);
        }
        Module module = framework.getModule(moduleDirName2ModuleName.get(moduleDirName));
        ComponentId actionComponentId = new ComponentId(actionName);
        if(!module.isComponentLoaded(actionComponentId)) {
            throw new ActionNotFoundException(moduleDirName, actionName);
        }
        Component component = module.getComponent(actionComponentId);
        if(component == null || !(component instanceof Action)) {
            throw new ActionNotFoundException(moduleDirName, actionName);
        }
        return (Action)component;
    }

    public static void invokeAction(String moduleDirName, String actionName, ActionContext actionContext) throws Exception {

        Action action = WebContextLoader.getAction(moduleDirName, actionName);
        if(action == null) {
            throw new ActionNotFoundException(moduleDirName, actionName);
        }
        action.execute(actionContext);
    }

    public static PageContext invokeAction(ActionContext actionContext) throws Exception {
        ActionContainer actionContainer = framework.getSystemModule().findComponentByInterface(ActionContainer.class).iterator().next();
        return actionContainer.invokeAction(actionContext);
    }

    public static void main(String[] args) {

    }
}
