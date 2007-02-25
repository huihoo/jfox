package net.sourceforge.jfox.mvc.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.jfox.mvc.Render;

/**
 * 每个模块都拥有独立的 VelocityEngine，便于使用相对路径 #parse
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TemplateServlet extends HttpServlet {

    /**
     * suffix => Render
     */
    private Map<String, Render> renderMap = new HashMap<String, Render>();
    
    public void init(ServletConfig config) throws ServletException {

//        Map<String, File> modulePath2File = ControllerServlet.getModulePath2FileMap();

        Enumeration enu = config.getInitParameterNames();
        try {
            while (enu.hasMoreElements()) {
                String renderName = (String)enu.nextElement();
                Render render = (Render)Class.forName(renderName).newInstance();

                render.init(config);
                String[] suffixes = config.getInitParameter(renderName).split(",");
                for(String suffix : suffixes){
                    int lastDotIndex = suffix.lastIndexOf(".");
                    if(lastDotIndex >= 0) {
                        renderMap.put(suffix.substring(lastDotIndex), render);
                    }
                    else {
                        renderMap.put(suffix, render);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new ServletException("Create render failed.",e);
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        int lastDotIndex = servletPath.lastIndexOf(".");
        String suffix = servletPath.substring(lastDotIndex);
        if(!renderMap.containsKey(suffix)) {
            throw new ServletException("Can not render servlet path: " + servletPath);
        }
        try {
            renderMap.get(suffix).render(request, response);
        }
        catch(Exception e) {
            throw new ServletException("Render failed.", e);
        }

    }

    public static void main(String[] args) {

    }
}
