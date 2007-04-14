package org.jfox.ejb3.security;

import java.util.Properties;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.component.SingletonComponent;
import org.jfox.mvc.SessionContext;

/**
 * 通过 HttpSession 来传播 Subject
 * 所以该 Component 一般在 Web 端进行调用
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service
public class JAASLoginServiceImpl implements JAASLoginService, ActiveComponent, ComponentInitialization, SingletonComponent {

    static Logger logger = Logger.getLogger(JAASLoginServiceImpl.class); 

    public static final String JAAS_CONFIG = "jaas.conf";
    public static final String ROLES_CONFIG = "roles.properties";

    private Configuration configuration;

    /**
     * ejb role=>application role
     */
    private Properties roleLink = new Properties();

    /**
     * 客户端的 thread subject
     */
//    static ThreadLocal<Subject> threadSubject = new ThreadLocal<Subject>();

    public JAASLoginServiceImpl() {
        
    }

    public void postContruct(ComponentContext componentContext) {
        
    }

    public void postInject() {
        try {
            String configURL = getClass().getClassLoader().getResource(JAAS_CONFIG).toString();
            // init configuration
//            configuration = new ConfigFile(getClass().getClassLoader().getResource(JAAS_CONFIG).toURI());
            // 兼容 1.5
            System.setProperty("java.security.auth.login.config",configURL);
            configuration = Configuration.getConfiguration();
            // load roles link
            //TODO: build roles link
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
     * @param callbackHandler 用来完成登录过程的 handler
     * @param params param array
     * @throws Exception if failed
     */
    public Object login(HttpServletRequest request, CallbackHandler callbackHandler, String... params) throws Exception {
        SessionContext sessionContext = SessionContext.init(request);
        return login(sessionContext,callbackHandler,params);
    }

    public Object login(SessionContext sessionContext, CallbackHandler callbackHandler, String... params) throws Exception {
        try {
            JAASLoginRequestCallback requestCallback = new JAASLoginRequestCallback();
            JAASLoginResponseCallback responseCallback = new JAASLoginResponseCallback();
            for (String param : params) {
                requestCallback.addParam(param);
            }
            loginRequestThreadLocal.set(requestCallback);
            loginResponseThreadLocal.set(responseCallback);

            LoginContext loginContext = new LoginContext("default", null, callbackHandler, configuration);
            loginContext.login();
            Subject subject = loginContext.getSubject();

            //把 Security 关联到 SessionContext 中
            if(sessionContext != null) {
                SecurityContext securityContext = new SecurityContext(subject);
                securityContext.setRoleLink(roleLink);
                sessionContext.bindSecurityContext(securityContext);
            }
            return responseCallback.getCallbackObject();
        }
        finally {
            loginRequestThreadLocal.remove();
            loginResponseThreadLocal.remove();
        }
    }
    public static void main(String[] args) throws Exception {
        JAASLoginServiceImpl loginService = new JAASLoginServiceImpl();
        loginService.postInject();
        loginService.login(SessionContext.currentThreadSessionContext(), new SampleCallbackHandler(), "YY", "1234");
    }
}
