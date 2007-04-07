package jfox.test.ejb3.stateless;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Calculator EJB Bean
 */
@Stateless(name = "stateless.CalculatorBean")
@Remote
@Local
public class CalculatorBean implements Calculator {

    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

}
