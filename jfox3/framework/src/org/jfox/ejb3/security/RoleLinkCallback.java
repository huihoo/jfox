package org.jfox.ejb3.security;

import java.security.Principal;
import javax.security.auth.callback.Callback;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface RoleLinkCallback extends Callback {

    /**
     * 得到一个用户对应的 EJB Security Role
     * @param applicationRole
     * @param principal
     */
    String[] getEJBSecurityRole(String applicationRole, Principal principal);



}
