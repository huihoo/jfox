/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import code.google.jcontainer.ContainerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * Web Context Loader，initialize framework when jfox3 web application loaded
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class WebContextLoader implements ServletContextListener {

    private static Log logger = LogFactory.getLog(WebContextLoader.class);

    private static ContainerFactory containerFactory;

    private static WebActionContainer webActionContainer= null;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //记录启动消耗时间
        long now = System.currentTimeMillis();
        containerFactory = ContainerFactory.scanPaths(new File(servletContextEvent.getServletContext().getRealPath("WEB-INF/classes")), new File(servletContextEvent.getServletContext().getRealPath("WEB-INF/lib")));
        webActionContainer = (WebActionContainer)containerFactory.getContainer("WebActionContainer");

        try {

            logger.info("WebActionContainer started in " + ((System.currentTimeMillis() - now) / 1000) + " seconds!");
        }
        catch (Exception e) {
            logger.error("WebActionContainer start failed!", e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //记录停止消耗时间
        long now = System.currentTimeMillis();
        try {
                System.gc();
                Thread.sleep(1000);
                logger.info("WebActionContainer stopped in " + ((System.currentTimeMillis() - now) / 1000) + " seconds!");
        }
        catch (Exception e) {
            servletContextEvent.getServletContext().log("Stop WebActionContainer failed!", e);
        }
    }

    public static void invokeAction(ActionContext actionContext) throws Throwable {
        webActionContainer.invokeAction(actionContext);
    }

    public static ContainerFactory getContainerFactory() {
        return containerFactory;
    }
}
