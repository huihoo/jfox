package net.sourceforge.jfox.mvc;

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
