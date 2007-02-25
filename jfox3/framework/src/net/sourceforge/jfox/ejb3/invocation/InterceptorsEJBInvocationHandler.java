package net.sourceforge.jfox.ejb3.invocation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import javax.interceptor.InvocationContext;

import net.sourceforge.jfox.ejb3.EJBInvocation;
import net.sourceforge.jfox.ejb3.EJBInvocationHandler;

/**
 * 有 AroundInvoke method 和 bean method 构成 interceptor chain
 * 最后一个 interceptor method 即可 bean method
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InterceptorsEJBInvocationHandler extends EJBInvocationHandler {

    public Object invoke(final EJBInvocation invocation, final Iterator<EJBInvocationHandler> chain) throws Exception {
        final Iterator<Method> it = invocation.getInterceptorMethods().iterator();
        final InvocationContext invocationContext = new InvocationContext() {
            
            /**
             * This allows an interceptor to save information in the context
             * data property of the InvocationContext that can be subsequently retrieved in other interceptors as
             * a means to pass contextual data between interceptors. The contextual data is not sharable across separate
             * business method invocations or lifecycle callback events. If interceptors are invoked as a result of
             * the invocation on a web service endpoint, the map returned by getContextData will be the
             * JAX-WS MessageContext [32]. The lifecycle of the InvocationContext instance is otherwise
             * unspecified.
             *
             * #ejb3-core-spec P308
             */
            public Map<String, Object> getContextData() {
                return Collections.emptyMap();
            }

            public Object getTarget() {
                return invocation.getTarget();
            }

            public Method getMethod() {
                return invocation.getMethod();
            }

            public Object[] getParameters() {
                return invocation.getArgs();
            }

            public void setParameters(Object[] objects) {
                invocation.setArgs(objects);
            }

            public Object proceed() throws Exception {
                return it.next().invoke(getTarget(), getParameters());
            }
        };
        // 这是最后一个 EJBInvocation，不再需要往后传递
        return invocationContext.proceed();
    }


    public static void main(String[] args) {

    }
}
