/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.servlet;

import code.google.webactioncontainer.ActionContext;
import code.google.webactioncontainer.PageContext;
import code.google.webactioncontainer.WebContextLoader;
import code.google.webactioncontainer.annotation.ActionMethod;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 控制器Servlet，所有的Servlet请求，均由该Servlet负责分发
 * <p/>
 * ControllerServlet缓存有所有Action，会根据 URL 规则交给正确的 WebAction 执行， Action则为一个纯粹的Java类，不依赖于Web容器
 * <p/>
 * ControllerServlet 使用 forward("/WEB-INF/xxx.html") 将请求发送到 /WEB-INF 中
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ControllerServlet extends HttpServlet {

    public static final String PAGE_CONTEXT = "__PAGE_CONTEXT__";
    public static final String MAX_UPLOAD_FILE_SIZE_KEY = "MAX_UPLOAD_FILE_SIZE";
    public static int MAX_UPLOAD_FILE_SIZE = 5 * 1000 * 1000;

    //主要用来控制request charactor encoding, File Upload, Velocity, Freemarker的编码
    public static String DEFAULT_ENCODING = "UTF-8";

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        // max upload limit
        String maxUploadFileSize = servletConfig.getServletContext().getInitParameter(MAX_UPLOAD_FILE_SIZE_KEY);
        if (maxUploadFileSize != null && maxUploadFileSize.trim().length() != 0) {
            MAX_UPLOAD_FILE_SIZE = Integer.parseInt(maxUploadFileSize);
        }

    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(DEFAULT_ENCODING);
        doAction(request, response);
    }

    protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        String queryString = request.getQueryString();
        if(servletPath.startsWith("/")) {
            servletPath = servletPath.substring(1);
        }
        String actionNameAndMethod = servletPath.substring(0, servletPath.lastIndexOf("."));
        int actionNameDotIndex = actionNameAndMethod.indexOf(".");
        String actionName = actionNameAndMethod.substring(0, actionNameDotIndex);
        String actionMethodName = actionNameAndMethod.substring(actionNameDotIndex + 1);

        // 调用 ActionContainer执行Action
        ActionContext actionContext = new ActionContext(getServletConfig(), actionName, actionMethodName, request);
        try {
            PageContext pageContext = actionContext.getPageContext();
            request.setAttribute(PAGE_CONTEXT, pageContext);
            WebContextLoader.invokeAction(actionContext);
            // 根据 PageContext.getTargetMethod 要决定 forward 还是 redirect
            if(pageContext.getForwardMethod().equals(ActionMethod.ForwardMethod.REDIRECT)) {
                response.sendRedirect(pageContext.getTargeView());
            }
            else {
                request.getRequestDispatcher(pageContext.getTargeView()).forward(request, response);
            }
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
        catch (Throwable throwable) {
            throw new ServletException(throwable);
        }

    }
}
