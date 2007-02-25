package net.sourceforge.jfox.framework.invoker;

import java.lang.reflect.Method;

import net.sourceforge.jfox.framework.BaseException;
import net.sourceforge.jfox.framework.ComponentId;
import net.sourceforge.jfox.framework.component.Component;

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
    public Object invokeMethod(Component theComponent, ComponentId componentId, Method method, Object... args) throws BaseException {
        return super.invokeMethod(theComponent, componentId, method, args);
    }

    public static void main(String[] args) {

    }
}
