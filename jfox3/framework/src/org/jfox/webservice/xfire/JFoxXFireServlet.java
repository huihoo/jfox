/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.webservice.xfire;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServlet;

import javax.servlet.ServletException;

/**
 * 用来接受 WebService 请求的 Servlet
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JFoxXFireServlet extends XFireServlet {

    //TODO: 获取 Session，并传递给 EJBContainer.invoke

    public XFire createXFire() throws ServletException {
        return XFireContainerInvoker.xFireFactory.getXFire();
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
