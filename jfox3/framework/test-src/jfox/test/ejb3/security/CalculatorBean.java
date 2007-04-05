package jfox.test.ejb3.security;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless(name = "security.CalculatorBean")
@Remote
@Local
@RunAs("role")
public class CalculatorBean implements CalculatorRemote, CalculatorLocal {

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
