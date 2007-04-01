package org.jfox.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.jfox.mvc.FileUploaded;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.WebContextLoader;
import org.jfox.mvc.annotation.ActionMethod;

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
    public static final String INVOCATION_CONTEXT = "__INVOCATION_CONTEXT__";
    public static final String SESSION_KEY = "__SESSION_KEY__";
    public static final String MAX_UPLOAD_FILE_SIZE_KEY = "MAX_UPLOAD_FILE_SIZE";
    public static final String VIEW_DIR_KEY = "VIEW_DIR";
    public static final String ACTION_SUFFIX_KEY = "ACTION_SUFFIX";
    public static final String DEFAULT_ENCODING_KEY = "DEFAULT_ENCODING";

    public static final String MULTIPART = "multipart/";

    public static String DEFAULT_ENCODING = "UTF-8";

    static String ACTION_SUFFIX = ".do";
    public static String VIEW_DIR = "views";
    static int MAX_UPLOAD_FILE_SIZE = 5 * 1000 * 1000;

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

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo.endsWith(ACTION_SUFFIX)) {
            // action
            forwardAction(request, response);
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
        String filePath = pathInfo.substring(slashIndex);
        String realPath = WebContextLoader.getModulePathByModuleDirName(moduleDirName) + "/" + VIEW_DIR + filePath;
        request.getRequestDispatcher(realPath).forward(request, response);
    }

    protected void forwardAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        int slashIndex = pathInfo.indexOf("/", 2);
        String moduleDirName = pathInfo.substring(1, slashIndex);
        if (!moduleDirName.contains(moduleDirName)) {
            throw new ServletException("Module " + moduleDirName + " is not exists!");
        }

        int dotDoIndex = pathInfo.lastIndexOf(ACTION_SUFFIX);
        int lastSlashIndex = pathInfo.lastIndexOf("/");
        int actionMethodDotIndex = pathInfo.indexOf(".", lastSlashIndex);
        String actionName = pathInfo.substring(lastSlashIndex + 1, actionMethodDotIndex);
        String actionMethodName = pathInfo.substring(actionMethodDotIndex + 1, dotDoIndex);

        InvocationContext invocationContext = new InvocationContext(getServletConfig(), actionMethodName);
        invocationContext.setPostMethod(request.getMethod().toUpperCase().equals("POST"));

        // 会导致取出的值为数组问题，所以只能使用下面的循环
        if (!isMultipartContent(request)) {
            for (Enumeration enu = request.getParameterNames(); enu.hasMoreElements();) {
                String key = (String)enu.nextElement();
                String[] values = request.getParameterValues(key);
                invocationContext.addParameter(key, values);
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
                            invocationContext.addParameter(fileItem.getFieldName(), new String[]{fileItem.getString(DEFAULT_ENCODING)});
                        }
                        else {
                            //  如果不是表单内容，取出 multipart。
                            //  上传文件路径和文件、扩展名。
                            String sourcePath = fileItem.getName();
                            //  获取真实文件名
                            String fileName = new File(sourcePath).getName();
                            // 读到内存成 FileUpload 对象
                            FileUploaded fileUploaded = new FileUploaded(fileName, fileItem.get());
                            invocationContext.addFileUploaded(fileItem.getFieldName(), fileUploaded);
                        }
                    }
                }
                catch (FileUploadException e) {
                    throw new ServletException("File upload failed!", e);
                }
            }
        }

        SessionContext sessionContext = (SessionContext)request.getSession().getAttribute(SESSION_KEY);
        if (sessionContext == null) {
            sessionContext = new SessionContext();
            request.getSession().setAttribute(SESSION_KEY, sessionContext);
        }
        invocationContext.setSessionContext(sessionContext);

        try {
            WebContextLoader.invokeAction(moduleDirName, actionName, invocationContext);
//            request.setAttribute(PAGE_CONTEXT, pageContext);
            request.setAttribute(INVOCATION_CONTEXT, invocationContext);
            //TODO: 根据 PageContext.getTargetMethod 要决定 forward 还是 redirect
            if(invocationContext.getPageContext().getTargetMethod().equals(ActionMethod.TargetMethod.REDIRECT)) {
                request.getRequestDispatcher(invocationContext.getPageContext().getTargeView()).forward(request, response);
            }
            else {
                request.getRequestDispatcher(invocationContext.getPageContext().getTargeView()).forward(request, response);
            }
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public static boolean isMultipartContent(HttpServletRequest req) {
        if (!"POST".equals(req.getMethod().toUpperCase())) {
            return false;
        }
        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith(MULTIPART)) {
            return true;
        }
        else {
            return false;
        }
    }
}
