package org.jfox.ejb3.security;

import javax.security.auth.callback.CallbackHandler;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface JAASLoginService {

    static ThreadLocal<JAASLoginRequestCallback> loginRequestThreadLocal = new ThreadLocal<JAASLoginRequestCallback>();

    boolean login(String... params) throws Exception;

    public boolean login(CallbackHandler callbackHandler, String... params) throws Exception;
}
