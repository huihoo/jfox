package org.jfox.ejb3.security;

import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.jfox.framework.component.Component;
import org.jfox.framework.annotation.Service;

/**
 * 根据 Callback的内容，实现登录
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Service
public class SampleCallbackHandler implements CallbackHandler, Component {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        JAASLoginRequestCallback requestLoginRequestCallback = (JAASLoginRequestCallback)callbacks[0];
        JAASLoginResponseCallback responseCallback = (JAASLoginResponseCallback)callbacks[1];
        responseCallback.setPrincipalId(requestLoginRequestCallback.getParams().get(0));
        
        System.out.println("SampleCallbackHandler.handle: " + Arrays.toString(callbacks));
    }

    public static void main(String[] args) {

    }
}
