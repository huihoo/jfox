package net.sourceforge.jfox.ejb3.transaction;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import net.sourceforge.jfox.ejb3.EJBContainerException;
import org.apache.log4j.Logger;
import org.objectweb.jotm.Jotm;

/**
 * 使用 JOTM 构造的 JTA TransactionManager
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JTATransactionManager implements TransactionManager {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private Jotm jotm;

    public static JTATransactionManager getIntsance(){
        return Holder.jtaTransactionManager;
    }

    private JTATransactionManager() {
        init();
    }

    public void init() {
        try {
            jotm = new Jotm(true, false);

            // 删除 carol 注册的全局变量 ServerConfiguration#124
            System.clearProperty(Context.URL_PKG_PREFIXES);
        }
        catch (NamingException e) {
            throw new EJBContainerException("Could not initialize JOTM!", e);
        }
        logger.info("JTA TransactionManager initalized.");
    }

    public void stop() {
        logger.debug("postUnregister");
        // 需要调用 stop 方法，以停止 jotm 的线程，使程序能正常终止
        jotm.stop();
        try {
            Thread.sleep(2000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void begin() throws NotSupportedException, SystemException {
        jotm.getTransactionManager().begin();
        final Transaction tx = jotm.getTransactionManager().getTransaction();
        TxConnectionsThreadLocal.setTransaction(tx);
        try {
            // 注册事务同步器，在事务 commit/rollback 的时候， 释放数据库连接
            tx.registerSynchronization(new Synchronization() {
                public void beforeCompletion() {
                }

                public void afterCompletion(int i) {
                    logger.debug("Release transaction attatched connections, transaction is: " + tx);
                    // releaseTxConnection, 由 EntityManagerImpl.getConnection 时add
                    TxConnectionsThreadLocal.releaseTxConnections();
                }
            });
        }
        catch (Exception e) {
            logger.warn("Register transaction synchronization exception.",e);
        }
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        jotm.getTransactionManager().commit();
    }

    public int getStatus() throws SystemException {
        return jotm.getTransactionManager().getStatus();
    }

    public Transaction getTransaction() throws SystemException {
        return jotm.getTransactionManager().getTransaction();
    }

    public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        jotm.getTransactionManager().resume(transaction);
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        //TODO: 如果已经自动 rollback，将无法调用 jotm.getTransactionManager, 抛 java.lang.IllegalStateException: Cannot get Transaction for rollback
        jotm.getTransactionManager().rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        jotm.getTransactionManager().setRollbackOnly();
    }

    public void setTransactionTimeout(int timeout) throws SystemException {
        jotm.getTransactionManager().setTransactionTimeout(timeout);
    }

    public Transaction suspend() throws SystemException {
        return jotm.getTransactionManager().suspend();
    }

    static class Holder {
        static JTATransactionManager jtaTransactionManager = new JTATransactionManager();
    }
}
