package org.jfox.ejb3.security;

import javax.security.auth.Subject;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface ApplicationLoginModule {

   Subject buildSubject();

}
