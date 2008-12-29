/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.invocation;

import org.jfox.ejb3.EJBInvocation;
import org.jfox.ejb3.EJBInvocationHandler;
import org.jfox.ejb3.transaction.EJBSynchronization;

import javax.ejb.SessionSynchronization;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TransactionEJBInvocationHandler extends EJBInvocationHandler {

    private static final String CREATED_TRANDACTION = "__CREATED_TRANSACTION__";
    private static final String COMMIT_TRANSACTION_OK = "__COMMIT_TRANSACTION_OK__";
    private static final String HAS_SUSPEND_TRANSACTION = "__HAS_SUSPEND_TRANSACTION__";
    private static final String EXCEPTION_THROWED = "__HAS_EXCEPTION__";

    public void invoke(final EJBInvocation invocation) throws Exception {
        logger.debug("invoke ejb invocation " + invocation);

        // 得到 JOTM TransactionManager
        TransactionManager tm = invocation.getTransactionManager();

        Transaction suspendedTrans = null;

        boolean isSync = false;
        //事务同步, 规范中，只有 stateful session bean 可以实现 SessionSynchronization 接口 p70
        if ((invocation.getTarget() instanceof SessionSynchronization)) {
            if(invocation.isStateful()) {
                isSync = true;
            }
            else {
                logger.warn("Only stateful session bean may implement javax.ejb.SessionSynchronization.");
            }
        }

        //得到的已经是实体方法
        Method method = invocation.getConcreteMethod();
        TransactionAttributeType type = TransactionAttributeType.REQUIRED;
        if (method.isAnnotationPresent(TransactionAttribute.class)) {
            type = method.getAnnotation(TransactionAttribute.class).value();
        }
        int status = tm.getStatus();

        /**
         * 由这个方法创建的事务，因为该方法可能集成其调用方法的事务，
         * 但是该方法不能 commit 该事务，而应该由其调用方法来 commit
         */
        boolean created = false;
        // 是否需要提交事务
        boolean toCommit = true;

//        Exception exception = null;
        try {
            switch (type) {
                case NOT_SUPPORTED: {
                    if (status != Status.STATUS_NO_TRANSACTION) {
                        suspendedTrans = tm.suspend();
                    }
                    break;
                }
                case SUPPORTS: {
                    break;
                }
                case REQUIRED: {
                    if (status == Status.STATUS_NO_TRANSACTION) {
                        tm.begin();
                        // tx synchronize
                        if (isSync) {
                            EJBSynchronization ejbSync = new EJBSynchronization((SessionSynchronization)invocation.getTarget(), tm.getTransaction());
                            tm.getTransaction().registerSynchronization(ejbSync);
                            ejbSync.afterBegin();
                        }
                        created = true;
                    }
                    break;
                }
                case REQUIRES_NEW: {
                    if (status != Status.STATUS_NO_TRANSACTION) {
                        suspendedTrans = tm.suspend();
                    }
                    tm.begin();
                    // tx synchronize
                    if (isSync) {
                        EJBSynchronization ejbSync = new EJBSynchronization((SessionSynchronization)invocation.getTarget(), tm.getTransaction());
                        tm.getTransaction().registerSynchronization(ejbSync);
                        ejbSync.afterBegin();
                    }
                    created = true;
                    break;
                }
                case MANDATORY: {
                    if (status == Status.STATUS_NO_TRANSACTION) {
                        throw new TransactionRequiredException("Transaction Required, TrasactionAttributeType.MANDATORY!");
                    }
                    break;
                }
                case NEVER: {
                    if (status != Status.STATUS_NO_TRANSACTION) {
                        throw new NotSupportedException("Transaction Not Supported, TrasactionAttributeType.NEVER!");
                    }
                    break;
                }
            }
        }
        finally {
            invocation.setAttribute(CREATED_TRANDACTION, created);
            invocation.setAttribute(COMMIT_TRANSACTION_OK, true);
//            invocation.setAttribute("EXCEPTION", exception);
            invocation.setAttribute(HAS_SUSPEND_TRANSACTION, suspendedTrans);
        }
    }

    @Override
    protected void onCaughtException(EJBInvocation invocation, Exception e) throws Exception {
        invocation.setAttribute(COMMIT_TRANSACTION_OK, false);
        invocation.setAttribute(EXCEPTION_THROWED, e);
        if( e instanceof InvocationTargetException) {
            throw (Exception)((InvocationTargetException)e).getTargetException();
        }
        else {
            throw e;
        }
    }

    @Override
    protected void onChainReturn(EJBInvocation invocation) throws Exception {
        TransactionManager tm = invocation.getTransactionManager();
        boolean created = (Boolean)invocation.getAttribute(CREATED_TRANDACTION);
        boolean toCommit = (Boolean)invocation.getAttribute(COMMIT_TRANSACTION_OK);
        Exception exception = (Exception)invocation.getAttribute(EXCEPTION_THROWED);
        Transaction suspendedTrans = (Transaction)invocation.getAttribute(HAS_SUSPEND_TRANSACTION);

        if (created) {
            if (toCommit) {
                if (tm.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    // 如果直接 commit 会抛出 RollbackException
                    if(exception != null) {
                        logger.info("Rollback transaction because exception caught for EJB invcation: " + invocation, exception);
                    }
                    tm.rollback();
                }
                else {
                    tm.commit();
                }
            }
            else {
                if(exception != null) {
                    logger.info("Rollback transaction because exception caught for EJB invcation: " + invocation, exception);
                }
                tm.rollback();
            }
        }
        else { // 如果没有新建事务，但是继承了事务并且当前线程抛出了异常，要标记当前事务为 rollback only
            if (!toCommit && tm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tm.getTransaction().setRollbackOnly();
            }
        }
        if (suspendedTrans != null) {
            tm.resume(suspendedTrans);
        }
    }

    public static void main(String[] args) {

    }
}
