package org.jfox.mvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用来处理使用.do访问 Action 的请求
 * 需要处理两种 url:
 * 1. /manager/index.page.do (含有路径)
 * 2. index.page.do (不含模块路径，即访问的是主模块)
 *
 * //TODO: 采用 *.do 来进行 url-pattern,不再需要 /m/*
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2008-9-18 1:06:14
 */
public class ActionServlet extends HttpServlet {

    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.service(httpServletRequest, httpServletResponse);
    }

    public static void main(String[] args) {

    }
}
