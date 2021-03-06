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

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TransactableComponentInvoker extends  ReflectComponentInvoker {

    /**
     * 支持 Transaction 的 ComponentInvoker
     * @param theComponent component instance
     * @param componentId componentId
     * @param method 要执行的方法
     * @param args 参数类标
     * @throws BaseException any exception
     */
    public Object invokeMethod(Component theComponent, ComponentId componentId, Method method, Object... args) throws Exception {
        return super.invokeMethod(theComponent, componentId, method, args);
    }

    public static void main(String[] args) {

    }
}
