package org.jfox.ejb3.security;

import java.util.Map;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class JAASLoginModule implements LoginModule {

    private CallbackHandler callbackHandler;

    public boolean abort() throws LoginException {
        return false;
    }

    public boolean commit() throws LoginException {
        return true;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        System.out.println("SampleLoginModule.initialize." + subject + ", " + callbackHandler);
    }

    public boolean login() throws LoginException {
        System.out.println("@@@@@@@@@@ login");

        JAASLoginRequestCallback loginRequestCallback = JAASLoginService.loginRequestThreadLocal.get();
        try {
            JAASLoginResultCallback loginResultCallback =  new JAASLoginResultCallback();
            callbackHandler.handle(new Callback[]{loginRequestCallback, loginResultCallback});
            // 处理 loginResultCallback
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean logout() throws LoginException {
        return false;
    }

    public static void main(String[] args) {

    }
}
