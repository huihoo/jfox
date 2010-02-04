/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface Render {

    void init(ServletConfig config) throws ServletException;

    /**
     * render output page, and write to response
     * @param request http request
     * @param response http response
     * @throws Exception exception
     */
    void render(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
