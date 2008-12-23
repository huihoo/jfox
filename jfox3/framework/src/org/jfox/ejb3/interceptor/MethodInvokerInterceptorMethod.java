/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.interceptor;

import javax.interceptor.InvocationContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * invoke Business Method, this must be last interceptor Method
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MethodInvokerInterceptorMethod implements InterceptorMethod {

    private Method businessMethod;

    public MethodInvokerInterceptorMethod(Method businessMethod) {
        this.businessMethod = businessMethod;
    }

    public Object invoke(InvocationContext invocationContext) throws Exception {
        try {
            return businessMethod.invoke(invocationContext.getTarget(), invocationContext.getParameters());
        }
        catch (InvocationTargetException e) {
            throw (Exception)e.getTargetException();
        }
    }

    public static void main(String[] args) {

    }
}
