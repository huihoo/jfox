package net.sourceforge.jfox.ejb3.interceptor;

import java.lang.reflect.Method;
import javax.interceptor.InvocationContext;
import javax.ejb.EJBException;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class SeperatedInterceptorMethod implements InterceptorMethod {

    private Method interceptorMethod;

    private Object interceptorInstance;

    public SeperatedInterceptorMethod(Class interceptorClass, Method interceptorMethod) {
        this.interceptorMethod = interceptorMethod;
        try {
            interceptorInstance = interceptorClass.newInstance();
        }
        catch(Exception e) {
            throw new EJBException("Could not create Interceptor class.", e);
        }
    }

    public Object invoke(InvocationContext invocationContext) throws Exception {
        return interceptorMethod.invoke(interceptorInstance,invocationContext.getParameters());
    }

    public static void main(String[] args) {

    }
}
