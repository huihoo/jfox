package jfox.test.ejb3.callback;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Stateless(name = "callback.CalculatorBean")
@Remote
@Local
public class CalculatorBean extends AbstractCalculatorBean {

    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println(this.getClass().getName() + ".postConstruct!");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println(this.getClass().getName() + ".preDestroy!");
    }
}
