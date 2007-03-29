package org.jfox.ejb3.security;

import java.io.File;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import com.sun.security.auth.login.ConfigFile;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class JAAS {

    public static void main(String[] args) throws Exception {
        Configuration configuration = new ConfigFile(new File("jaas.conf").toURI());
//        Configuration configuration = Configuration.
        LoginContext loginContext = new LoginContext("default", null, new SampleCallbackHandler(), configuration);
        loginContext.login();
    }
}
