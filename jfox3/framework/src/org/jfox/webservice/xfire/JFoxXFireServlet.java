/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.webservice.xfire;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServlet;
import org.jfox.mvc.WebContextLoader;
import org.jfox.webservice.WebServiceContainer;

import javax.servlet.ServletException;
import java.util.Collection;

/**
 * 用来接受 WebService 请求的 Servlet
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JFoxXFireServlet extends XFireServlet {

    public XFire createXFire() throws ServletException {
        // always get XFire from XFireJFoxService, singleton
        Collection<WebServiceContainer> webServiceContainers = WebContextLoader.getManagedFramework().getSystemModule().findComponentByInterface(WebServiceContainer.class);
        if(webServiceContainers.isEmpty()) {
            throw new ServletException("XFire is not initialized.");
        }
        XFire xfire = null;
        for(WebServiceContainer wsc : webServiceContainers){
            Object engine = wsc.getWebServiceEngine();
            if(engine instanceof XFire) {
                xfire = (XFire)engine;
                break;
            }
        }
        if(xfire == null){
            throw new ServletException("XFire is not initialized.");
        }
        return xfire;
    }

    public void init() throws ServletException {
        super.init(); 
    }

    public void destroy() {
        super.destroy();
    }

    public static void main(String[] args) {

    }
}
