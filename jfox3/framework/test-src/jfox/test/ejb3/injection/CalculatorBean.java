package jfox.test.ejb3.injection;

import javax.ejb.Stateless;

@Stateless(name="injection.CalculatorBean")
public class CalculatorBean implements Calculator {
    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }
}
