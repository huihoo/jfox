package org.jfox.framework.invoker;

import java.lang.reflect.Method;

import org.jfox.framework.BaseException;
import org.jfox.framework.ComponentId;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInvocationException;

/**
 * 在同一个JVM中，直接通过反射调用 Component
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ReflectComponentInvoker implements ComponentInvoker {

    public Object invokeMethod(Component theComponent, ComponentId componentId, Method method, Object... args) throws BaseException {
/*
        Component component;
        //TODO: 似乎不可能有不相等的情况???，因为总是通过Component引用调用自己的方法
        if (!theComponentContext.getComponentId().equals(componentId)) {
            component = theComponentContext.getComponent(componentId);
            //上面得到的component是 Component的动态代理引用，需要的得到 Component 的实体对象
            if(Proxy.isProxyClass(component.getClass())) {
                component = ((RefComponent)component).__getConcreteComponent();
            }
        }
        else {
            component = theComponentContext.getConcreteComponent();
        }
*/

        try {
            //TODO: get the concrete method of component implementation
            //TODO: cache Concreate Method
            return method.invoke(theComponent, args);
        }
        catch (Exception e) {
            throw new ComponentInvocationException("Invoke method " + theComponent.getClass().getName() + "." + method.getName() + " failed, ComponentId is " + componentId, e);
        }
    }

    public static void main(String[] args) {

    }
}
