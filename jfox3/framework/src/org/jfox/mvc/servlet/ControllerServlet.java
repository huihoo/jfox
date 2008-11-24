/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc.servlet;

import org.jfox.mvc.ActionContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.WebContextLoader;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.controller.ControllerInvocationHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 控制器Servlet，所有的Servlet请求，均由该Servlet负责分发
 * <p/>
 * ControllerServlet缓存有所有Action，会根据 URL 规则交给正确的 Action 执行， Action则为一个纯粹的Java类，不依赖于Web容器
 * <p/>
 * ControllerServlet 使用 forward("/WEB-INF/xxx.html") 将请求发送到 /WEB-INF 中
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ControllerServlet extends HttpServlet {

    public static final String PAGE_CONTEXT = "__PAGE_CONTEXT__";
//    public static final String ACTION_CONTEXT = "__INVOCATION_CONTEXT__";
    public static final String MAX_UPLOAD_FILE_SIZE_KEY = "MAX_UPLOAD_FILE_SIZE";
    public static final String VIEW_DIR_KEY = "VIEW_DIR";
    public static final String ACTION_SUFFIX_KEY = "ACTION_SUFFIX";
    public static final String DEFAULT_ENCODING_KEY = "DEFAULT_ENCODING";

//    public static final String MULTIPART = "multipart/";

    //主要用来控制request charactor encoding, File Upload, Velocity, Freemarker的编码
    public static String DEFAULT_ENCODING = "UTF-8";

    static String ACTION_SUFFIX = ".do";
    static String VIEW_DIR = "views";
    public static int MAX_UPLOAD_FILE_SIZE = 5 * 1000 * 1000;

    // controller invocation chain
    //TODO: add invocationHandlerChain
    private static final List<ControllerInvocationHandler> controllerInvocationHandlerChain = new ArrayList<ControllerInvocationHandler>(5);

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        String actionSuffix = servletConfig.getServletContext().getInitParameter(ACTION_SUFFIX_KEY);
        if (actionSuffix != null && actionSuffix.trim().length() != 0) {
            ACTION_SUFFIX = actionSuffix;
        }
        //default encoding
        String defaultEncoding = servletConfig.getServletContext().getInitParameter(DEFAULT_ENCODING_KEY);
        if (defaultEncoding != null && defaultEncoding.trim().length() != 0) {
            DEFAULT_ENCODING = defaultEncoding;
        }

        //view dir
        String viewDir = servletConfig.getServletContext().getInitParameter(VIEW_DIR_KEY);
        if (viewDir != null && viewDir.trim().length() != 0) {
            VIEW_DIR = viewDir;
        }

        // max upload limit
        String maxUploadFileSize = servletConfig.getServletContext().getInitParameter(MAX_UPLOAD_FILE_SIZE_KEY);
        if (maxUploadFileSize != null && maxUploadFileSize.trim().length() != 0) {
            MAX_UPLOAD_FILE_SIZE = Integer.parseInt(maxUploadFileSize);
        }

    }

    public static String getActionSuffix(){
        return ACTION_SUFFIX;
    }

    public static String getViewDir(){
        return VIEW_DIR;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO: 以 /m/root 访问 webroot

        request.setCharacterEncoding(DEFAULT_ENCODING);
        String pathInfo = request.getPathInfo();
        if (pathInfo.endsWith(ACTION_SUFFIX)) {
            // action
            forwardAction(request, response);
            Iterator<ControllerInvocationHandler> chain = controllerInvocationHandlerChain.iterator();
//            chain.next().invoke(actionContext, chain);
//            controllerInvocationHandlerChain.iterator().next().invoke(actionContext, controllerInvocationHandlerChain);
        }
        else {
            // static page or template
            forwardView(request, response);
        }
    }

    protected void forwardView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        int slashIndex = pathInfo.indexOf("/", 2);
        String moduleDirName = pathInfo.substring(1, slashIndex);
        if(moduleDirName.equals(UrlRewriteFilter.MAIN_MODULE)) {
            String realPath = pathInfo.substring(slashIndex);
            request.getRequestDispatcher(realPath).forward(request, response);
        }
        else {
            if(!WebContextLoader.isModuleExists(moduleDirName)) {
                throw new ServletException("Invalid request url: " + request.getRequestURL());
            }
            String filePath = pathInfo.substring(slashIndex);
            String realPath = WebContextLoader.getModulePathByModuleDirName(moduleDirName) + "/" + VIEW_DIR + filePath;
            request.getRequestDispatcher(realPath).forward(request, response);
        }
    }

    protected void forwardAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        int slashIndex = pathInfo.indexOf("/", 2);
        String moduleDirName = pathInfo.substring(1, slashIndex);
        if(!WebContextLoader.isModuleExists(moduleDirName)) {
            throw new ServletException("Invalid request url: " + request.getRequestURL());
        }

        int dotDoIndex = pathInfo.lastIndexOf(ACTION_SUFFIX);
        int lastSlashIndex = pathInfo.lastIndexOf("/");
        int actionMethodDotIndex = pathInfo.indexOf(".", lastSlashIndex);
        String actionName = pathInfo.substring(lastSlashIndex + 1, actionMethodDotIndex);
        String actionMethodName = pathInfo.substring(actionMethodDotIndex + 1, dotDoIndex);

        // 调用 ActionContainer执行Action
        ActionContext actionContext = new ActionContext(getServletConfig(),moduleDirName,actionName, actionMethodName, request);
        try {
            PageContext pageContext = actionContext.getPageContext();
            request.setAttribute(PAGE_CONTEXT, pageContext);
            WebContextLoader.invokeAction(actionContext);
            // 根据 PageContext.getTargetMethod 要决定 forward 还是 redirect
            if(pageContext.getForwardMethod().equals(ActionMethod.ForwardMethod.REDIRECT)) {
                response.sendRedirect(pageContext.getTargeView());
//                request.getRequestDispatcher(invocationContext.getPageContext().getTargeView()).(request, response);
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
/*

        // 会导致取出的值为数组问题，所以只能使用下面的循环
        final Map<String,String[]> parameterMap = new HashMap<String, String[]>();
        final Map<String, FileUploaded> fileUploadedMap = new HashMap<String, FileUploaded>();
        if (!isMultipartContent(request)) {
            for (Enumeration enu = request.getParameterNames(); enu.hasMoreElements();) {
                String key = (String)enu.nextElement();
                String[] values = request.getParameterValues(key);
//                invocationContext.addParameter(key, values);
                if(queryString != null && queryString.contains(key)) { // 是 URL encoding
                    values = decodeQueryStringParameterValues(values);
                }
                parameterMap.put(key,values);
            }
        }
        else { // 有文件上传
            //  从 HTTP servlet 获取 fileupload 组件需要的内容
            RequestContext requestContext = new ServletRequestContext(request);
            //  判断是否包含 multipart 内容
            if (ServletFileUpload.isMultipartContent(requestContext)) {
                //  创建基于磁盘的文件工厂
                DiskFileItemFactory factory = new DiskFileItemFactory();
                //  设置直接存储文件的极限大小，一旦超过则写入临时文件以节约内存。默认为 1024 字节
                factory.setSizeThreshold(MAX_UPLOAD_FILE_SIZE);
                //  创建上传处理器，可以处理从单个 HTML 上传的多个上传文件。
                ServletFileUpload upload = new ServletFileUpload(factory);
                //  最大允许上传的文件大小 5M
                upload.setSizeMax(MAX_UPLOAD_FILE_SIZE);
                upload.setHeaderEncoding(DEFAULT_ENCODING);
                try {
                    //  处理上传
                    List items = upload.parseRequest(requestContext);
                    //  由于提交了表单字段信息，需要进行循环区分。
                    for (Object item : items) {
                        FileItem fileItem = (FileItem)item;
                        if (fileItem.isFormField()) {
                            // 表单内容
//                            invocationContext.addParameter(fileItem.getFieldName(), new String[]{fileItem.getString(DEFAULT_ENCODING)});
                            parameterMap.put(fileItem.getFieldName(), new String[]{fileItem.getString(DEFAULT_ENCODING)});
                        }
                        else {
                            //  如果不是表单内容，取出 multipart。
                            //  上传文件路径和文件、扩展名。
                            String sourcePath = fileItem.getName();
                            //  获取真实文件名
                            String fileName = new File(sourcePath).getName();
                            // 读到内存成 FileUpload 对象
                            FileUploaded fileUploaded = new FileUploaded(fileItem.getFieldName(), fileName, fileItem.get());
//                            invocationContext.addFileUploaded(fileItem.getFieldName(), fileUploaded);
                            fileUploadedMap.put(fileItem.getFieldName(), fileUploaded);
                        }
                    }
                }
                catch (FileUploadException e) {
                    throw new ServletException("File upload failed!", e);
                }
            }
        }

        try {
            // 初始化 SessionContext，并绑定到线程
            ActionContext actionContext = new ActionContext(getServletConfig(), request, parameterMap, fileUploadedMap, actionName, actionMethodName);
            request.setAttribute(ACTION_CONTEXT, actionContext);
            WebContextLoader.invokeAction(moduleDirName, actionName, actionContext);
            // 根据 PageContext.getTargetMethod 要决定 forward 还是 redirect
            if(actionContext.getPageContext().getTargetMethod().equals(ActionMethod.ForwardMethod.REDIRECT)) {
                response.sendRedirect(actionContext.getPageContext().getTargeView());
//                request.getRequestDispatcher(invocationContext.getPageContext().getTargeView()).(request, response);
            }
            else {
                request.getRequestDispatcher(actionContext.getPageContext().getTargeView()).forward(request, response);
            }
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
*/
        
    }

/*
    public static boolean isMultipartContent(HttpServletRequest req) {
        if (!"POST".equals(req.getMethod().toUpperCase())) {
            return false;
        }
        String contentType = req.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(MULTIPART);
    }

    public String[] decodeQueryStringParameterValues(String[] values) {
        if(values != null && values.length > 0) {
            String[] decodedValues = new String[values.length];
            for(int i=0; i<values.length; i++) {
                try {
                    decodedValues[i] = new String(values[i].getBytes("ISO-8859-1"), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    decodedValues[i] = values[i];
                }
            }
            return decodedValues;
        }
        else {
            return new String[0];
        }
    }
*/
}
