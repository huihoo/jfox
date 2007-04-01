package jfox.test.ejb3.interceptor;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.interceptor.Interceptors;

@Stateless(name = "interceptor.CalculatorBean")
@Remote
@Local
@Interceptors({OuterClassInterceptor.class})
public class CalculatorBean extends SuperCalculatorBean implements CalculatorRemote, CalculatorLocal {

    @Resource
    SessionContext sessionContext;

    public int add(int x, int y) {
        return x + y;
    }

    @Interceptors({OuterMethodInterceptor.class})
    public int subtract(int x, int y) {
        return x - y;
    }

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {
        System.out.println("AroundInvoke: " + this.getClass().getName() + ".aroundInvoke, method: " + invocationContext.getMethod().getName());
        return invocationContext.proceed();
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println(this.getClass().getName() + ".postConstruct!");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println(this.getClass().getName() + ".preDestroy!");
    }
}
