package net.sourceforge.jfox.ejb3.interceptor;

import java.lang.reflect.Method;
import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class EmbeddedInterceptorMethod implements InterceptorMethod {

    private Method interceptorMethod;

    public EmbeddedInterceptorMethod(Method interceptorMethod) {
        this.interceptorMethod = interceptorMethod;
    }

    public Object invoke(InvocationContext invocationContext) throws Exception {
        return interceptorMethod.invoke(invocationContext.getTarget(), invocationContext);
    }

    public static void main(String[] args) {

    }
}
