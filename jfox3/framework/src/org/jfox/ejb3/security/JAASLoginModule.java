/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.security;

import java.util.Map;
import java.util.List;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JAASLoginModule implements LoginModule {

    static Logger logger = Logger.getLogger(JAASLoginModule.class);

    private Subject subject = null;
    private CallbackHandler callbackHandler = null;

    public boolean abort() throws LoginException {
        return false;
    }

    public boolean commit() throws LoginException {
        return true;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        logger.debug("initialize, callbackHandler: " + callbackHandler);
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    public boolean login() throws LoginException {
        JAASLoginRequestCallback loginRequestCallback = JAASLoginService.loginRequestThreadLocal.get();
        JAASLoginResponseCallback loginResponseCallback =  JAASLoginService.loginResponseThreadLocal.get();
        try {
            callbackHandler.handle(new Callback[]{loginRequestCallback, loginResponseCallback});

            // 处理 loginResultCallback，构造 Subject, 设置 SecurityContext
            String principalName = loginResponseCallback.getPrincipalName();

            subject.getPrincipals().add(new JAASPrincipal(principalName));
            
            //initialize 中 的 subject
            List<String> roles = loginResponseCallback.getRoles();
            SecurityContext.initSubject(subject, principalName, roles);

        }
        catch(Exception e) {
            logger.warn("Login failed.", e);
            throw new LoginException(e.getMessage());
        }

        return true;
    }

    public boolean logout() throws LoginException {
        return false;
    }

    public static void main(String[] args) {

    }
}
