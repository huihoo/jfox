package org.jfox.ejb3.security;

import java.util.Properties;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import com.sun.security.auth.login.ConfigFile;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInstantiation;
import org.jfox.framework.component.SingletonComponent;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Service
public class JAASLoginServiceImpl implements JAASLoginService, ActiveComponent, ComponentInstantiation, SingletonComponent {

    public static final String JAAS_CONFIG = "jaas.conf";
    public static final String ROLES_CONFIG = "roles.properties";

    private Configuration configuration;

    private Properties roleLink = new Properties();

    @Inject
    private CallbackHandler callbackHandler;

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
            e.printStackTrace();
        }

    }

    /**
     * 登录
     * 需要将 params 构造成 JAASLoginRequestCallback
     *
     * @param params param array
     * @throws Exception if failed
     */
    public boolean login(String... params) throws Exception {
        return login(callbackHandler,params);
    }

    public boolean login(CallbackHandler callbackHandler, String... params) throws Exception {

        try {
            JAASLoginRequestCallback loginRequestCallback = new JAASLoginRequestCallback();
            for (String param : params) {
                loginRequestCallback.addParam(param);
            }
            loginRequestThreadLocal.set(loginRequestCallback);

            LoginContext loginContext = new LoginContext("default", null, callbackHandler, configuration);
            loginContext.login();
            return true;
        }
        finally {
            loginRequestThreadLocal.remove();
        }
    }

    public static void main(String[] args) throws Exception {
        JAASLoginServiceImpl loginService = new JAASLoginServiceImpl();
        loginService.postPropertiesSet();
        loginService.login(new SampleCallbackHandler(), "YY", "1234");
    }
}
