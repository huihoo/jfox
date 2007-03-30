package org.jfox.ejb3.security;

import java.io.File;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import com.sun.security.auth.login.ConfigFile;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class JAAS {
    //TODO: 配置用户自己的 CallbackHandler
    /**
     * 登录
     * 需要将 params 构造成 JAASLoginRequestCallback
     * @param params param array
     * @throws Exception if failed
     */
    public boolean login(String... params) throws Exception {

        Configuration configuration = new ConfigFile(new File("framework/conf/jaas.conf").toURI());
//        Configuration configuration = Configuration.
        LoginContext loginContext = new LoginContext("default", null, new SampleCallbackHandler(), configuration);
        loginContext.login();
        return true;
    }

    public static void main(String[] args) throws Exception {
        new JAAS().login("YY","1234");

    }
}
