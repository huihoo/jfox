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

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(WebContextLoader.getEncoding());
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
