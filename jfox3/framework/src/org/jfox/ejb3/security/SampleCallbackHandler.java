/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.security;

import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * 根据 Callback的内容，实现登录
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SampleCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        JAASLoginRequestCallback requestLoginRequestCallback = (JAASLoginRequestCallback)callbacks[0];
        JAASLoginResponseCallback responseCallback = (JAASLoginResponseCallback)callbacks[1];

        String username = requestLoginRequestCallback.getParams().get(0);
        String password = requestLoginRequestCallback.getParams().get(1);

        // set principal id
        responseCallback.setPrincipalName(username);
        // set role
        responseCallback.addRole(username);

        // setCallbackObject
        responseCallback.setCallbackObject(username);
        
        System.out.println("SampleCallbackHandler.handle: " + Arrays.toString(callbacks));
    }
}
