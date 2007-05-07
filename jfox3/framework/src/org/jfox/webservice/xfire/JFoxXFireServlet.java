/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.webservice.xfire;

import javax.servlet.ServletException;

import org.codehaus.xfire.transport.http.XFireServlet;
import org.codehaus.xfire.XFire;

/**
 * 用来接受 WebService 请求的 Servlet
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JFoxXFireServlet extends XFireServlet {

    public XFire createXFire() throws ServletException {
        // get XFire from XFireJFoxService
        return JFoxXFireDelegate.getXFireInstance();
    }

    public static void main(String[] args) {

    }
}
