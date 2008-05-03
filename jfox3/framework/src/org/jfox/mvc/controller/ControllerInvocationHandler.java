package org.jfox.mvc.controller;

import org.apache.log4j.Logger;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ControllerException;

import java.util.Iterator;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Apr 26, 2008 12:05:35 AM
 */
public abstract class ControllerInvocationHandler {

    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 交给 chain 的下一个 handler 处理
     *
     * @param actionContext
     * @param chain invocation chain
     * @throws Exception any exception
     */
    protected final Object next(final ActionContext actionContext, final Iterator<ControllerInvocationHandler> chain) throws Exception {
        if(chain.hasNext()){
            return chain.next().invoke(actionContext,chain);
        }
        else {
            throw new ControllerException("no ActionInvocationHandler!");
        }
    }

    /**
     * 对 ejb invocation 进行处理
     * @param actionContext
     * @param chain invocation chain
     * @throws Exception any exception
     */
    public abstract Object invoke(final ActionContext actionContext, final Iterator<ControllerInvocationHandler> chain) throws Exception;

    public static void main(String[] args) {

    }
}
