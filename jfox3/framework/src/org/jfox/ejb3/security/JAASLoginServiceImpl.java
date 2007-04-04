package org.jfox.ejb3.security;

import java.util.Properties;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import com.sun.security.auth.login.ConfigFile;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInstantiation;
import org.jfox.framework.component.SingletonComponent;
import org.jfox.mvc.SessionContext;
import org.apache.log4j.Logger;

/**
 * 通过 HttpSession 来传播 Subject
 * 所以该 Component 一般在 Web 端进行调用
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Service
public class JAASLoginServiceImpl implements JAASLoginService, ActiveComponent, ComponentInstantiation, SingletonComponent {

    static Logger logger = Logger.getLogger(JAASLoginServiceImpl.class); 

    public static final String JAAS_CONFIG = "jaas.conf";
    public static final String ROLES_CONFIG = "roles.properties";

    private Configuration configuration;

    /**
     * ejb role=>application role
     */
    private Properties roleLink = new Properties();

    @Inject
    private CallbackHandler callbackHandler;

    /**
     * 客户端的 thread subject
     */
//    static ThreadLocal<Subject> threadSubject = new ThreadLocal<Subject>();

    public JAASLoginServiceImpl() {
        
    }

    public void postContruct(ComponentContext componentContext) {
        
    }

    public void postPropertiesSet() {
        try {
            // init configuration
            configuration = new ConfigFile(getClass().getClassLoader().getResource(JAAS_CONFIG).toURI());
            // load roles link
            roleLink.load(getClass().getClassLoader().getResourceAsStream(ROLES_CONFIG));
        }
        catch(Exception e) {
            logger.error("Initialize JAASLoginService failed!", e);
        }
    }

    /**
     * 登录
     * 需要将 params 构造成 JAASLoginRequestCallback
     *
     * @param sessionContext http session context
     * @param params param array
     * @throws Exception if failed
     */
    public boolean login(SessionContext sessionContext, String... params) throws Exception {
        return login(sessionContext, callbackHandler, params);
    }

    public boolean login(HttpServletRequest request, String... params) throws Exception {
        SessionContext sessionContext = SessionContext.init(request);
        return login(sessionContext, params);
    }
    /**
     * 登录
     * 需要将 params 构造成 JAASLoginRequestCallback
     *
     * @param callbackHandler 用来完成登录过程的 handler
     * @param params param array
     * @throws Exception if failed
     */
    public boolean login(HttpServletRequest request, CallbackHandler callbackHandler, String... params) throws Exception {
        SessionContext sessionContext = SessionContext.init(request);
        return login(sessionContext,callbackHandler,params);
    }

    public boolean login(SessionContext sessionContext, CallbackHandler callbackHandler, String... params) throws Exception {
        try {
            JAASLoginRequestCallback loginRequestCallback = new JAASLoginRequestCallback();
            for (String param : params) {
                loginRequestCallback.addParam(param);
            }
            loginRequestThreadLocal.set(loginRequestCallback);

            LoginContext loginContext = new LoginContext("default", null, callbackHandler, configuration);
            loginContext.login();
            Subject subject = loginContext.getSubject();
            //把 Subject 关联到 SessionContext 中
            if(sessionContext != null) {
                sessionContext.associateSubject(subject);
            }
            return true;
        }
        finally {
            loginRequestThreadLocal.remove();
        }
    }
    public static void main(String[] args) throws Exception {
        JAASLoginServiceImpl loginService = new JAASLoginServiceImpl();
        loginService.postPropertiesSet();
        loginService.login((SessionContext)null, new SampleCallbackHandler(), "YY", "1234");
    }
}
