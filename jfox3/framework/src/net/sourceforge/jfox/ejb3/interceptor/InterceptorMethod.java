package net.sourceforge.jfox.ejb3.interceptor;

import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface InterceptorMethod {

    Object invoke(InvocationContext invocationContext) throws Exception;
    
}
