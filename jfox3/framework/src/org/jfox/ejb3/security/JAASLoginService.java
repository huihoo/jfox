package org.jfox.ejb3.security;

import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletRequest;

import org.jfox.mvc.SessionContext;
import org.jfox.framework.component.Component;
import org.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface JAASLoginService extends Component {

    static ThreadLocal<JAASLoginRequestCallback> loginRequestThreadLocal = new ThreadLocal<JAASLoginRequestCallback>();
    static ThreadLocal<JAASLoginResponseCallback> loginResponseThreadLocal = new ThreadLocal<JAASLoginResponseCallback>();

    public Object login(HttpServletRequest request, CallbackHandler callbackHandler, String... params) throws Exception;
    
    public Object login(SessionContext sessionContext, CallbackHandler callbackHandler, String... params) throws Exception;
}
