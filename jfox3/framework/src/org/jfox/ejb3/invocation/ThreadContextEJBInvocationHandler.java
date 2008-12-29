/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.invocation;

import org.jfox.ejb3.EJBInvocation;
import org.jfox.ejb3.EJBInvocationHandler;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ThreadContextEJBInvocationHandler extends EJBInvocationHandler {

    private static final String THREAD_KEY = "__THREAD_CLASS_LOADER__";

    public void invoke(final EJBInvocation invocation) throws Exception {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        invocation.setAttribute(THREAD_KEY, oldLoader);
        // 设置 ThreadContextClassLoader，以便执行 ejb 方法时，能正确装载需要的类
        Thread.currentThread().setContextClassLoader(invocation.getTarget().getClass().getClassLoader());
        // 将 invocation 关联到 Thread，以便 EJBContainer可以 获得 java:comp/env
        EJBInvocation.setCurrent(invocation);
    }

    @Override
    protected void onChainReturn(EJBInvocation invocation) throws Exception {
        // 解决 invocation 和线程关联
        EJBInvocation.remove();        
        // 恢复 ThreadContextClassLoader
        ClassLoader oldLoader = (ClassLoader)invocation.getAttribute(THREAD_KEY);
        Thread.currentThread().setContextClassLoader(oldLoader);
    }

    public static void main(String[] args) {

    }
}
