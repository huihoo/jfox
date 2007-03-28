package org.jfox.framework.component;

import java.lang.reflect.Method;

/**
 * 可进行方法拦截的组件。实现该接口的组件，在执行器方法的时候，可以在方法执行前后，进行额外的操作
 * <br>
 * <b>注意：</b>该功能通过Java动态代理技术实现
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface InterceptableComponent extends Component{
    /**
     * do something before invoke a method defined in component interface
     * example: want to log, or repack the params
     *
     * @param method method to invoke
     * @param params parameter array
     * @return true/false, if false, will not invoke the method
     */
    boolean preInvoke(Method method, Object[] params);

    /**
     * do something after invoke a method example: want to repack the result
     *
     * @param method method invoked
     * @param params parameter array
     * @param result result return by the method
     * @param exception if throws exception
     * @return the last object return, may not be the result parameter
     */
    Object postInvoke(Method method, Object[] params, Object result, Throwable exception);

}
