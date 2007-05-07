/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
