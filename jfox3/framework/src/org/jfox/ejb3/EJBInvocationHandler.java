/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

import org.apache.log4j.Logger;

/**
 * Chain Invocation Handler
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class EJBInvocationHandler {

    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 交给 chain 的下一个 handler 处理
     *
     * @param invocation ejb invocation
     * @throws Exception any exception
     */
    protected final void process(final EJBInvocation invocation) throws Exception {
/*
        boolean canNext = true;
*/
        try {
            invoke(invocation); // 执行操作，如果有异常，将会执行 finally，并抛出异常
            invocation.chainNext(); // 执行下一个 invocationHandler，如果有异常，将会执行 finally，并抛出异常
        }
        catch (Exception e) {
            onCaughtException(invocation, e);
        }
/*
        catch (Exception e) {
            // 抛出异常，无法继续, chain无法继续
            throw e;
        }
*/
        finally {
            onChainReturn(invocation);
        }
    }

    protected void onCaughtException(EJBInvocation invocation, Exception e) throws Exception{
        throw e; // default throw all exception
    }

    /**
     * Chain 执行完成，返回时，需要进行的操作，总是会在 throw exception 或者 return 之前运行
     * @param invocation invocation
     * @throws Exception e
     */
    protected void onChainReturn(final EJBInvocation invocation) throws Exception {

    }

    /**
     * 对 ejb invocation 进行处理
     * @param invocation ejb invocation
     * @throws Exception any exception
     */
    public abstract void invoke(final EJBInvocation invocation) throws Exception;
}
