package org.jfox.mvc.servlet;

import org.jfox.mvc.WebContextLoader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2008-9-23 15:09:19
 */
public class UrlRewriteFilter implements Filter {

    public static final String MAIN_MODULE = "_web_root_";

    private String moduleAccessPrefix = "m";

    public void init(FilterConfig filterConfig) throws ServletException {
        moduleAccessPrefix = filterConfig.getServletContext().getInitParameter("MODULE_ACCESS_PREFIX");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest hsRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse hsResponse = (HttpServletResponse) servletResponse;
        String servletPath = hsRequest.getServletPath();// /manager/console.sysinfo.do
        int firstSlashIndex = servletPath.indexOf("/", 1);
        String rewrittenUrl;
        if (firstSlashIndex < 0) { // 确定访问的是主模块
            rewrittenUrl = "/" + moduleAccessPrefix +  "/" + MAIN_MODULE + servletPath;
        }
        else {
            String guessModule = servletPath.substring(1, firstSlashIndex);
            if (servletPath.endsWith(ControllerServlet.getActionSuffix())) { // 是 action，那么一级目录肯定为模块
                rewrittenUrl = "/" + moduleAccessPrefix + servletPath;
            }
            else {// 访问的是静态文件
                if (WebContextLoader.isModuleExists(guessModule)) { // 存在该模块
                    rewrittenUrl = "/" + moduleAccessPrefix + servletPath;
                }
                else { //不存在该模块，认为是访问的主模块中的目录
                    rewrittenUrl = "/" + moduleAccessPrefix + "/" + MAIN_MODULE + servletPath;
                }
            }
        }

        RequestDispatcher dispatcher = servletRequest.getRequestDispatcher(rewrittenUrl);
        dispatcher.forward(hsRequest, new UrlRewriteResponseWrapper(hsResponse, rewrittenUrl));
//        filterChain.doFilter(servletRequest, new UrlRewriteResponseWrapper((HttpServletResponse)servletResponse));
    }

    public void destroy() {

    }

    public class UrlRewriteResponseWrapper extends HttpServletResponseWrapper {
        private String rewrittenUrl;

        public UrlRewriteResponseWrapper(HttpServletResponse httpServletResponse, String rewrittenUrl) {
            super(httpServletResponse);
            this.rewrittenUrl = rewrittenUrl;
        }

        public String encodeURL(String s) {
            return super.encodeURL(rewrittenUrl);
        }

        public String encodeRedirectURL(String s) {
            return super.encodeRedirectURL(rewrittenUrl);
        }

        public String encodeUrl(String s) {
            return encodeURL(s);
        }

        public String encodeRedirectUrl(String s) {
            return encodeRedirectURL(s);
        }
    }

}
