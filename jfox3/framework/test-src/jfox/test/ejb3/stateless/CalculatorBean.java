package jfox.test.ejb3.stateless;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless(name = "stateless.CalculatorBean")
@Remote
@Local
public class CalculatorBean implements CalculatorRemote, CalculatorLocal {

    @Resource
    SessionContext sessionContext;

    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

}
