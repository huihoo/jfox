package org.jfox.ejb3.interceptor;

import javax.interceptor.InvocationContext;

/**
 * Bean 中的 @Interceptors @InvokeAround 描述的方法
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface InterceptorMethod {

    Object invoke(InvocationContext invocationContext) throws Exception;
    
}
