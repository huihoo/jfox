/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.invocation;

import java.util.Iterator;

import org.jfox.ejb3.EJBInvocation;
import org.jfox.ejb3.EJBInvocationHandler;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ThreadContextEJBInvocationHandler extends EJBInvocationHandler {

    public Object invoke(final EJBInvocation invocation, final Iterator<EJBInvocationHandler> chain) throws Exception {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        // 设置 ThreadContextClassLoader，以便执行 ejb 方法时，能正确装载需要的类
        Thread.currentThread().setContextClassLoader(invocation.getTarget().getClass().getClassLoader());
        // 将 invocation 关联到 Thread，以便 EJBContainer可以 获得 java:comp/env
        EJBInvocation.setCurrent(invocation);

        try {

            return super.next(invocation, chain);
        }
        finally {
            // 解决 invocation 和线程关联 
            EJBInvocation.remove();
            // 恢复 ThreadContextClassLoader
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    public static void main(String[] args) {

    }
}
