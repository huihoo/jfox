package net.sourceforge.jfox.ejb3.interceptor;

import java.lang.reflect.Method;
import javax.interceptor.InvocationContext;

/**
 * Interceptor Method annotated by @AroundInvoke
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InternalInterceptorMethod implements InterceptorMethod {

    private Method interceptorMethod;

    public InternalInterceptorMethod(Method interceptorMethod) {
        this.interceptorMethod = interceptorMethod;
    }

    public Object invoke(InvocationContext invocationContext) throws Exception {
        return interceptorMethod.invoke(invocationContext.getTarget(), invocationContext);
    }

    public static void main(String[] args) {

    }
}
