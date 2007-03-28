package org.jfox.ejb3.interceptor;

import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface InterceptorMethod {

    Object invoke(InvocationContext invocationContext) throws Exception;
    
}
