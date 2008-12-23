/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.interceptor;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * Interceptor Method annotated by @AroundInvoke
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class AroundInvokeInterceptorMethod implements InterceptorMethod {

    private Method interceptorMethod;

    public AroundInvokeInterceptorMethod(Method interceptorMethod) {
        this.interceptorMethod = interceptorMethod;
    }

    public Object invoke(InvocationContext invocationContext) throws Exception {
        return interceptorMethod.invoke(invocationContext.getTarget(), invocationContext);
    }

    public static void main(String[] args) {

    }
}
