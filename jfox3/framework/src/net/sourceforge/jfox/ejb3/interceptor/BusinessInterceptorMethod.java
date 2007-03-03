package net.sourceforge.jfox.ejb3.interceptor;

import java.lang.reflect.Method;
import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
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
