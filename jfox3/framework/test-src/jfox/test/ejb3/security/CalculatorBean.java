/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.security;

import java.security.Principal;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.SessionContext;

@Stateless(name = "security.CalculatorBean")
@Remote
@Local
@RunAs("role")
public class CalculatorBean implements CalculatorRemote, CalculatorLocal {

    @Resource
    SessionContext ejbContext;

    @PermitAll
    public int add(int x, int y) {
        return x + y;
    }

    @DenyAll
    public int subtract(int x, int y) {
        return x - y;
    }

    @RolesAllowed({"role1,role2"})
    public int plus(int x, int y) {
        return x*y;
    }

    public double devide(int x, int y) {
        Principal principal = ejbContext.getCallerPrincipal();
        System.out.println("devide caller principal: " + principal);
        return x/y;
    }

}
