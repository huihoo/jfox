/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public abstract class SuperCalculatorBean implements CalculatorRemote, CalculatorLocal {

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception{
        System.out.println("AroundInvoke: " + SuperCalculatorBean.class.getName() + ".aroundInvoke, method: " + invocationContext.getMethod().getName());
        return invocationContext.proceed();
    }

    @AroundInvoke
    public Object superAroundInvoke(InvocationContext invocationContext) throws Exception{
        System.out.println("AroundInvoke: " + SuperCalculatorBean.class.getName() + ".superAroundInvoke, method: " + invocationContext.getMethod().getName());
        return invocationContext.proceed();
    }
}
