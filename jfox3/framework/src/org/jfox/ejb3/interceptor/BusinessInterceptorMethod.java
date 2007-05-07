/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.interceptor;

import java.lang.reflect.Method;
import javax.interceptor.InvocationContext;

/**
 * Business Method to invoke
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class BusinessInterceptorMethod implements InterceptorMethod {

    private Method businessMethod;

    public BusinessInterceptorMethod(Method businessMethod) {
        this.businessMethod = businessMethod;
    }

    public Object invoke(InvocationContext invocationContext) throws Exception {
        return businessMethod.invoke(invocationContext.getTarget(), invocationContext.getParameters());
    }

    public static void main(String[] args) {

    }
}
