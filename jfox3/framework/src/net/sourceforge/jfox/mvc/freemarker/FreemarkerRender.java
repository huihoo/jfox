package net.sourceforge.jfox.mvc.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.Render;
import net.sourceforge.jfox.mvc.WebContextLoader;
import net.sourceforge.jfox.mvc.servlet.ControllerServlet;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class FreemarkerRender implements Render {
    /**
     * Encoding for the output stream
     */
    public static final String DEFAULT_OUTPUT_ENCODING = "ISO-8859-1";

    private String outputEncoding = "UTF-8";

    /**
     * The default content type for the response
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    private String defaultContentType = DEFAULT_CONTENT_TYPE;


    /**
     * 每个 Module 对应的 Configuration
     * module view base dir => module's freemarker Configuration
     */
    private static Map<String, Configuration> baseDir2ConfigurationMap = new HashMap<String, Configuration>();

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public void init(ServletConfig config) throws ServletException {
        Map<String, File> modulePath2File = WebContextLoader.getModulePath2FileMap();

        try {
            for (Map.Entry<String, File> entry : modulePath2File.entrySet()) {
                String modulePath = entry.getKey();
                File moduleDir = entry.getValue();
                String templateBaseDir = moduleDir.getCanonicalFile().getAbsoluteFile().getPath() + "/" + ControllerServlet.VIEW_DIR;

                outputEncoding = ControllerServlet.DEFAULT_ENCODING;

                Configuration configuration = new Configuration();
                configuration.setDefaultEncoding(ControllerServlet.DEFAULT_ENCODING);
                configuration.setOutputEncoding(ControllerServlet.DEFAULT_ENCODING);
                configuration.setDirectoryForTemplateLoading(new File(templateBaseDir));

                // 注册相对 module template base dir，以便根据访问的 URL，来判断访问的Module，获得 VelocityEngine
                baseDir2ConfigurationMap.put(modulePath + "/" + ControllerServlet.VIEW_DIR, configuration);
            }
        }
        catch (Exception e) {
            throw new ServletException("Error initializing Velocity: " + e, e);
        }

    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //TODO: detect ContentType
        response.setContentType(getContentType());
        Locale locale = request.getLocale();
        String servletPath = request.getServletPath();
        processTemplate(getTemplate(servletPath, locale), createFreemarkerContext(request, response), response);
    }

    /**
     * create VelocityContext and set key/value
     *
     * @param request  http request
     * @param response http response
     */
    protected Map createFreemarkerContext(HttpServletRequest request, HttpServletResponse response) {
        InvocationContext invocationContext = (InvocationContext)request.getAttribute(ControllerServlet.INVOCATION_CONTEXT);
        PageContext pageContext = invocationContext.getPageContext();
        Map<String, Object> freemarkerMap = new HashMap<String, Object>(pageContext.getResultMap());
        freemarkerMap.put("exception", pageContext.getBusinessException());

        freemarkerMap.put("request", request);
        freemarkerMap.put("REQUEST", request);
        freemarkerMap.put("WEBAPP_CONTEXT_PATH", request.getContextPath());
//        velocityContext.put("MODULE_CONTEXT_PATH", request.getContextPath() + "/" + module);
        Object sessionContext = request.getSession().getAttribute(ControllerServlet.SESSION_KEY);
        freemarkerMap.put("session", sessionContext);
        freemarkerMap.put("SESSION", sessionContext);
        freemarkerMap.put("sessionContext", sessionContext);
        freemarkerMap.put("pageCtx", pageContext);
        freemarkerMap.put("pageContext", pageContext);
        //用于在页面上显示 vm 文件全路径，便于调试
        freemarkerMap.put("__VIEW__", request.getServletPath());

        return freemarkerMap;
    }

    protected Template getTemplate(String servletPath, Locale locale) throws IOException {
        // 首先要根据 servletPath 获得所访问的 Module，进而得到 Module 的 VelocityEngine，用 startsWith 判断？
        String templateName = null;
        Configuration engine = null;
        for (Map.Entry<String, Configuration> entry : baseDir2ConfigurationMap.entrySet()) {
            String baseDir = entry.getKey();
            Configuration configuration = entry.getValue();
            int index = servletPath.indexOf(baseDir);
            if (index >= 0) {
                engine = configuration;
                templateName = servletPath.substring(index + baseDir.length());
            }
        }
        if (engine == null) {
            throw new IOException("Can not found velocity engine for servlet path: " + servletPath);
        }

        return engine.getTemplate(templateName, locale, outputEncoding);
    }

    /**
     * Process the FreeMarker template to the servlet response.
     * <p>Can be overridden to customize the behavior.
     *
     * @param template the template to process
     * @param model    the model for the template
     * @param response servlet response (use this to get the OutputStream or Writer)
     * @throws IOException       if the template file could not be retrieved
     * @throws TemplateException if thrown by FreeMarker
     * @see freemarker.template.Template#process(Object,java.io.Writer)
     */
    protected void processTemplate(Template template, Map model, HttpServletResponse response)
            throws IOException, TemplateException {
        template.process(model, response.getWriter());
    }

    /**
     * Sets the content type of the response, defaulting to {@link
     * #defaultContentType} if not overriden.
     *
     * @param request  The servlet request from the client.
     * @param response The servlet reponse to the client.
     */
    protected void setContentType(HttpServletRequest request, HttpServletResponse response) {
        String contentType = defaultContentType;
        int index = contentType.lastIndexOf(';') + 1;
        if (index <= 0 || (index < contentType.length() &&
                contentType.indexOf("charset", index) == -1)) {
            // Append the character encoding which we'd like to use.
            String encoding = outputEncoding;
            //System.out.println("Chose output encoding of '" +
            //                   encoding + '\'');
            if (!DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding)) {
                contentType += "; charset=" + encoding;
            }
        }
        response.setContentType(contentType);
        //System.out.println("Response Content-Type set to '" +
        //                   contentType + '\'');
    }

    public static void main(String[] args) {

    }
}
