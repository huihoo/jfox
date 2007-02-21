package net.sourceforge.jfox.mvc;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.mvc.servlet.ControllerServlet;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class WebContextLoader implements ServletContextListener {

    public static final String MODULES = "MODULES";

    private Framework framework = null;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        framework = new Framework();
        try {
            String toDeployModules = servletContextEvent.getServletContext().getInitParameter(MODULES);
            String[] modulePaths = toDeployModules.split(",");
            for(String modulePath : modulePaths){
                File moduleDir = new File(servletContextEvent.getServletContext().getRealPath("/"), modulePath);
                framework.loadModule(moduleDir);
                // register module path
                ControllerServlet.registerModulePath(modulePath, moduleDir);
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

    public static void main(String[] args) {

    }
}
