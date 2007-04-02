package org.jfox.ejb3.security;

import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * 根据 Callback的内容，实现登录
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class SampleCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        JAASLoginRequestCallback requestLoginRequestCallback = (JAASLoginRequestCallback)callbacks[0];
        JAASLoginResultCallback resultCallback = (JAASLoginResultCallback)callbacks[1];
        
        System.out.println("SampleCallbackHandler.handle: " + Arrays.toString(callbacks));
    }

    public static void main(String[] args) {

    }
}
