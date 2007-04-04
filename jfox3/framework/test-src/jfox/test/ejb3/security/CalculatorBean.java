package jfox.test.ejb3.security;

import javax.annotation.Resource;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless(name = "stateless.CalculatorBean")
@Remote
@Local
@RunAs("role1")
public class CalculatorBean implements CalculatorRemote, CalculatorLocal {

    @Resource
    SessionContext sessionContext;

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
        return x/y;
    }

}
