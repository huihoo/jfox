package org.jfox.example.ejb3.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class OuterClassInterceptor {

    @AroundInvoke
    public Object aourndInvoke(InvocationContext invocationContext) throws Exception{
        System.out.println("AroundInvoke: " + this.getClass().getName() + ".aroundInvoke, method: " + invocationContext.getMethod().getName());
        return invocationContext.proceed();
    }
}
