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

    /**
     * 登录接口方法，如果在Servlet中直接调用，则可以使用该方法
     *
     * @param request http servlet request
     * @param callbackHandler 进行登录的callback，调用时由client指定
     * @param params 登录时提供的参数，一般是用户名和密码
     * @return 登录成功之后的对象，比如：Account对象，取决于callbackHandler调用JAASLoginResponseCallback.setCallbackObject
     * @throws Exception 登录失败时抛出异常
     */
    public Object login(HttpServletRequest request, CallbackHandler callbackHandler, String... params) throws Exception;

    /**
     * 登录接口方法，如果在Action中调用，则可以使用该方法，因为能够得到SessionContext
     *
     * @param sessionContext session context
     * @param callbackHandler 进行登录的callback，调用时由client指定
     * @param params 登录时提供的参数，一般是用户名和密码
     * @return 登录成功之后的对象，比如：Account对象，取决于callbackHandler调用JAASLoginResponseCallback.setCallbackObject
     * @throws Exception 登录失败时抛出异常
     */
    public Object login(SessionContext sessionContext, CallbackHandler callbackHandler, String... params) throws Exception;
}
