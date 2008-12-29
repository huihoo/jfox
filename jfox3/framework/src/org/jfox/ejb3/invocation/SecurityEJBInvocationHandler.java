/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.invocation;

import org.jfox.ejb3.EJBInvocation;
import org.jfox.ejb3.EJBInvocationHandler;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBAccessException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * SecurityEJBInvocationHandler
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SecurityEJBInvocationHandler extends EJBInvocationHandler {

    public void invoke(final EJBInvocation invocation) throws Exception {
        Method method = invocation.getConcreteMethod();

        if(!method.isAnnotationPresent(PermitAll.class) && method.isAnnotationPresent(DenyAll.class)) {
            throw new EJBAccessException("DenyAll Roles to invoke EJB method: "+ invocation);
        }

        if(method.isAnnotationPresent(RolesAllowed.class)){
            String[] allowedRoles = (method.getAnnotation(RolesAllowed.class)).value();
            List<? extends Principal> callerPrincipals = invocation.getCallerRolesList();
            List<String> callerRoles = new ArrayList<String>(callerPrincipals.size());
            for(Principal p : callerPrincipals){
                callerRoles.add(p.getName());
            }
            if(Collections.disjoint(Arrays.asList(allowedRoles), callerRoles)){
                throw new EJBAccessException("Deny roles" + callerRoles + " to invoke : "+ invocation);
            }
        }
    }

    public static void main(String[] args) {

    }
}
