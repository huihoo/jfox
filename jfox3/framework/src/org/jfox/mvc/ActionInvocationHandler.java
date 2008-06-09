/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import org.apache.log4j.Logger;

import javax.ejb.EJBException;
import java.util.Iterator;

/**
 * Chain Invocation Handler
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class ActionInvocationHandler {

    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 交给 chain 的下一个 handler 处理
     *
     * @param invocation ejb invocation
     * @param chain invocation chain
     * @throws Exception any exception
     */
    protected final PageContext next(final ActionContext invocation, final Iterator<ActionInvocationHandler> chain) throws Exception {
        if(chain.hasNext()){
            return chain.next().invoke(invocation,chain);
        }
        else {
            throw new EJBException("no EJBInvocationHandler!");
        }
    }

    /**
     * 对 action invocation 进行处理
     * @param invocation ejb invocation
     * @param chain invocation chain
     * @throws Exception any exception
     */
    public abstract PageContext invoke(final ActionContext invocation, final Iterator<ActionInvocationHandler> chain) throws Exception;

    public static void main(String[] args) {

    }
}