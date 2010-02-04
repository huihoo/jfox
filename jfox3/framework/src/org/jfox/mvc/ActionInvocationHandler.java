/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import code.google.jcontainer.invoke.Invocation;
import code.google.jcontainer.invoke.InvocationHandler;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * Chain Invocation Handler
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class ActionInvocationHandler implements InvocationHandler {

    protected final Logger logger = Logger.getLogger(this.getClass());

    public void chainInvoke(Invocation invocation) throws Exception {

    }

    public void chainReturn(Invocation invocation) throws Exception {

    }

    public void onCaughtException(Invocation invocation, Exception e) {

    }

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
            throw new ActionException("no InvocationHandler!");
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