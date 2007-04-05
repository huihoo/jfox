package org.jfox.ejb3.security;

import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletRequest;

import org.jfox.mvc.SessionContext;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface JAASLoginService {

    static ThreadLocal<JAASLoginRequestCallback> loginRequestThreadLocal = new ThreadLocal<JAASLoginRequestCallback>();

    public boolean login(HttpServletRequest request, CallbackHandler callbackHandler, String... params) throws Exception;
    
    public boolean login(SessionContext sessionContext, CallbackHandler callbackHandler, String... params) throws Exception;
}
