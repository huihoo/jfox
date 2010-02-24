/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.freemarker;

import code.google.webactioncontainer.WebContextLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import code.google.webactioncontainer.PageContext;
import code.google.webactioncontainer.Render;
import code.google.webactioncontainer.servlet.ControllerServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FreemarkerRender implements Render {
    static Logger logger = Logger.getLogger(FreemarkerRender.class);
    /**
     * Encoding for the output stream
     */
    public static final String DEFAULT_OUTPUT_ENCODING = "ISO-8859-1";

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

    private Configuration engine;

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public void init(ServletConfig config) throws ServletException {
        logger.info("Initializaing Freemarker...");
        try {
            String templateBaseDir = config.getServletContext().getRealPath(".");

            Configuration configuration = new Configuration();
            configuration.setDefaultEncoding(WebContextLoader.getEncoding());
            configuration.setOutputEncoding(WebContextLoader.getEncoding());
            configuration.setDirectoryForTemplateLoading(new File(templateBaseDir));
            engine = configuration;
            logger.info("Freemarker engine initialized!");
        }
        catch (Exception e) {
            logger.error("Error initializing Freemarker: " + e.getMessage(), e);
        }
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        PageContext pageContext = (PageContext) request.getAttribute(ControllerServlet.PAGE_CONTEXT);
        Map<String, Object> freemarkerMap = new HashMap<String, Object>(pageContext.getResultMap());
        return freemarkerMap;
    }

    protected Template getTemplate(String servletPath, Locale locale) throws IOException {
        return engine.getTemplate(servletPath, locale);
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
    protected void processTemplate(Template template, Map model, HttpServletResponse response) throws IOException, TemplateException {
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
            String encoding = WebContextLoader.getEncoding();
            //System.out.println("Chose output encoding of '" +
            //                   encoding + '\'');
            if (!DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding)) {
                contentType += "; charset=" + encoding;
            }
        }
        response.setContentType(contentType);
    }

    public static void main(String[] args) {

    }
}
