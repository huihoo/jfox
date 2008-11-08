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
public class ActionServlet extends HttpServlet {

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
        String actionSuffix = servletConfig.getInitParameter(ACTION_SUFFIX_KEY);
        if (actionSuffix != null && actionSuffix.trim().length() != 0) {
            ACTION_SUFFIX = actionSuffix;
        }
        //default encoding
        String defaultEncoding = servletConfig.getInitParameter(DEFAULT_ENCODING_KEY);
        if (defaultEncoding != null && defaultEncoding.trim().length() != 0) {
            DEFAULT_ENCODING = defaultEncoding;
        }

        //view dir
        String viewDir = servletConfig.getInitParameter(VIEW_DIR_KEY);
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
        request.setCharacterEncoding(DEFAULT_ENCODING);
        String servletPath = request.getServletPath();
        if (servletPath.endsWith(ACTION_SUFFIX)) {
            // action
            executeAction(request, response);
            Iterator<ControllerInvocationHandler> chain = controllerInvocationHandlerChain.iterator();
//            chain.next().invoke(actionContext, chain);
//            controllerInvocationHandlerChain.iterator().next().invoke(actionContext, controllerInvocationHandlerChain);
        }
        else {
            // static page or template
            staticView(request, response);
        }


/*
        if (servletPath.endsWith(ACTION_SUFFIX)) {
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
*/
    }

    protected void executeAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //url format: 1) /actionname.actionmethod.do  2) /module/actionname.actionmethod.do
        String servletPath = request.getServletPath();
        if(servletPath.startsWith("/")) {
            servletPath = servletPath.substring(1);
        }
        String[] servletPathSplits = servletPath.split("/");

        String moduleDirName = "";
        String actionName;
        String actionMethodName;

        if(servletPathSplits.length == 1) { // action.actionmethod.do
            int firstDotIndex = servletPathSplits[0].indexOf(".");
            int secondDotIndex = servletPathSplits[0].lastIndexOf(".");
            actionName = servletPathSplits[0].substring(0, firstDotIndex);
            actionMethodName = servletPathSplits[0].substring(firstDotIndex+1, secondDotIndex);
        }
        else if(servletPathSplits.length == 2) { // moduleDirName/action.actionmethod.do
            moduleDirName = servletPathSplits[0];
            int firstDotIndex = servletPathSplits[1].indexOf(".");
            int secondDotIndex = servletPathSplits[1].lastIndexOf(".");
            actionName = servletPathSplits[1].substring(0, firstDotIndex);
            actionMethodName = servletPathSplits[1].substring(firstDotIndex+1, secondDotIndex);
        }
        else {
            //不合法的 .do URL
            throw new ServletException(new IllegalArgumentException("URL: " + request.getRequestURI()));
        }
        if(!moduleDirName.equals("")) {
            if(!WebContextLoader.isModuleExists(moduleDirName)) {
                throw new ServletException("Invalid request url: " + request.getRequestURL());
            }
        }

        // 调用 ActionContainer执行Action
        ActionContext actionContext = new ActionContext(getServletConfig(),moduleDirName,actionName, actionMethodName, request);
        try {
            PageContext pageContext = WebContextLoader.invokeAction(actionContext);
            request.setAttribute(PAGE_CONTEXT, pageContext);

            if(!WebContextLoader.isModuleExists(moduleDirName)) {
                throw new ServletException("Invalid request url: " + request.getRequestURL());
            }
            String realPath = WebContextLoader.getModulePathByModuleDirName(moduleDirName) + "/" + VIEW_DIR + pageContext.getTargeView();
            // 根据 PageContext.getTargetMethod 要决定 forward 还是 redirect
            if(pageContext.getForwardMethod().equals(ActionMethod.ForwardMethod.REDIRECT)) {
                response.sendRedirect(realPath);
//                request.getRequestDispatcher(invocationContext.getPageContext().getTargeView()).(request, response);
            }
            else {
                request.getRequestDispatcher(realPath).forward(request, response);
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

    protected void staticView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //url format: 1) /xxx.vhtml  2) /module/xxx.vhtml 3) /directory/xxx.vhtml
        String servletPath = request.getServletPath();
        if(servletPath.startsWith("/")) {
            servletPath = servletPath.substring(1);
        }
        String moduleDirName = "";
        String filePath = "";
        if(servletPath.indexOf("/") < 0){
            filePath = servletPath;
        }
        else {
            int firstSlashIndex = servletPath.indexOf("/");
            String _moduleDirName = servletPath.substring(0, firstSlashIndex);
            if(WebContextLoader.isModuleExists(moduleDirName)) { // 该 module 存在
                moduleDirName = _moduleDirName;
                filePath = servletPath.substring(firstSlashIndex + 1);
            }
            else { // Module 不存在，则视为普通目录
                filePath = servletPath;
            }
        }
        String realPath = filePath;
        if(!moduleDirName.equals("")) {
            realPath = WebContextLoader.getModulePathByModuleDirName(moduleDirName) + "/" + VIEW_DIR + filePath;
        }
        request.getRequestDispatcher(realPath).forward(request, response);
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