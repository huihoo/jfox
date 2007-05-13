/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
        try {
            // theComponent is the concrete component
            return method.invoke(theComponent, args);
        }
        catch (Exception e) {
            throw new ComponentInvocationException("Invoke method " + theComponent.getClass().getName() + "." + method.getName() + " failed, ComponentId is " + componentId, e);
        }
    }

    public static void main(String[] args) {

    }
}
