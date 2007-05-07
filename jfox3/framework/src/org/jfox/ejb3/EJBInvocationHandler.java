/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

import java.util.Iterator;
import javax.ejb.EJBException;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class EJBInvocationHandler {

    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 交给 chain 的下一个 handler 处理
     *
     * @param invocation ejb invocation
     * @param chain invocation china
     * @throws Exception any exception
     */
    protected final Object next(final EJBInvocation invocation, final Iterator<EJBInvocationHandler> chain) throws Exception {
        if(chain.hasNext()){
            return chain.next().invoke(invocation,chain);
        }
        else {
            throw new EJBException("no EJBInvocationHandler!");
        }
    }

    /**
     * 对 ejb invocation 进行处理
     * @param invocation ejb invocation
     * @param chain invocation china
     * @throws Exception any exception
     */
    public abstract Object invoke(final EJBInvocation invocation, final Iterator<EJBInvocationHandler> chain) throws Exception;

    public static void main(String[] args) {

    }
}
