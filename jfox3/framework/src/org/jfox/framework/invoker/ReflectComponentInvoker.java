/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jfox.framework.ComponentId;
import org.jfox.framework.component.Component;

/**
 * 在同一个JVM中，直接通过反射调用 Component
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ReflectComponentInvoker implements ComponentInvoker {

    private static Logger logger = Logger.getLogger(ReflectComponentInvoker.class);

    public Object invokeMethod(Component theComponent, ComponentId componentId, Method method, Object... args) throws Exception {
        try {
            // theComponent is the concrete component
            return method.invoke(theComponent, args);
        }
        catch (InvocationTargetException e) {
            logger.error("Invoke method " + theComponent.getClass().getName() + "." + method.getName() + " failed, ComponentId is " + componentId, e.getTargetException());
            Exception ex = (Exception)e.getTargetException();
            throw ex;
        }
        catch (Exception e) {
            logger.error("Invoke method " + theComponent.getClass().getName() + "." + method.getName() + " failed, ComponentId is " + componentId, e);
            throw e;
        }
    }

    public static void main(String[] args) {

    }
}
