package org.jfox.ejb3.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;

import org.jfox.ejb3.EJBInvocation;
import org.jfox.ejb3.EJBInvocationHandler;
import org.jfox.ejb3.transaction.EJBSynchronization;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TransactionEJBInvocationHandler extends EJBInvocationHandler {

    public Object invoke(final EJBInvocation invocation, final Iterator<EJBInvocationHandler> chain) throws Exception {
        logger.debug("invoke ejb invocation " + invocation);

        // 得到 JOTM TransactionManager
        TransactionManager tm = invocation.getTransactionManager();

        Transaction suspendedTrans = null;
        boolean isSync = (invocation.getTarget() instanceof SessionSynchronization);

/*
        // TODO: 事务同步, 规范中，只有 stateful session bean 可以实现 SessionSynchronization 接口
        if(isSync) {
            if(!meta.isSession())
                throw new EJBException("only stateful session bean can implement javax.ejb.SessionSynchronization.");
            else {
                if(((SessionDescriptor) meta).isStateless()) {
                    throw new EJBException("only stateful session bean can implement javax.ejb.SessionSynchronization.");
                }
            }
        }
*/
        //得到的已经是实体方法
        Method method = invocation.getConcreteMethod();
        TransactionAttributeType type = TransactionAttributeType.REQUIRED;
        if(method.isAnnotationPresent(TransactionAttribute.class)) {
            type = method.getAnnotation(TransactionAttribute.class).value();
        }
        int status = tm.getStatus();

        /**
         * 由这个方法创建的事务，因为该方法可能集成其调用方法的事务，
         * 但是该方法不能 commit 该事务，而应该由其调用方法来 commit
         */
        boolean created = false;
        boolean toCommit = true;
        try {
            switch(type) {
                case NOT_SUPPORTED:
                    {
                        if(status != Status.STATUS_NO_TRANSACTION) {
                            suspendedTrans = tm.suspend();
                        }
                        break;
                    }
                case SUPPORTS:
                    {
                        break;
                    }
                case REQUIRED:
                    {
                        if(status == Status.STATUS_NO_TRANSACTION) {
                            tm.begin();
                            // tx synchronize
                            if(isSync) {
                                EJBSynchronization ejbSync = new EJBSynchronization((SessionSynchronization) invocation.getTarget(), tm.getTransaction());
                                tm.getTransaction().registerSynchronization(ejbSync);
                                ejbSync.afterBegin();
                            }
                            created = true;
                        }
                        break;
                    }
                case REQUIRES_NEW:
                    {
                        if(status != Status.STATUS_NO_TRANSACTION) {
                            suspendedTrans = tm.suspend();
                        }
                        tm.begin();
                        // tx synchronize
                        if(isSync) {
                            EJBSynchronization ejbSync = new EJBSynchronization((SessionSynchronization) invocation.getTarget(), tm.getTransaction());
                            tm.getTransaction().registerSynchronization(ejbSync);
                            ejbSync.afterBegin();
                        }
                        created = true;
                        break;
                    }
                case MANDATORY:
                    {
                        if(status == Status.STATUS_NO_TRANSACTION) {
                            throw new TransactionRequiredException("Transaction Required, TrasactionAttributeType.MANDATORY!");
                        }
                        break;
                    }
                case NEVER:
                    {
                        if(status != Status.STATUS_NO_TRANSACTION) {
                            throw new NotSupportedException("Transaction Not Supported, TrasactionAttributeType.NEVER!");
                        }
                        break;
                    }
            }
            return next(invocation, chain);
        }
        catch(EJBException e) {
            // only catch EJBException, rollback the transaction
            // Application Exception maybe right logic
            toCommit = false;
            throw e;
        }
        catch(SQLException e) {
            // only catch EJBException, rollback the transaction
            // Application Exception maybe right logic
            toCommit = false;
            throw e;
        }
        catch(PersistenceException e) {
            // only catch EJBException, rollback the transaction
            // Application Exception maybe right logic
            toCommit = false;
            throw e;
        }
        catch(InvocationTargetException e) {
            toCommit = false;
            throw (Exception)e.getTargetException();
        }
        catch(Exception e) {
            toCommit = false;
            throw e;
        }
        finally {
            if(created) {
                if(toCommit) {
                    if(tm.getStatus() == Status.STATUS_MARKED_ROLLBACK){
                        // 如果直接 commit 会抛出 RollbackException
                        tm.rollback();
                    }
                    else {
                        tm.commit();
                    }                    
                }
                else {
                    tm.rollback();
                }
            }
            else { // 如果没有新建事务，但是继承了事务并且当前线程抛出了异常，要标记当前事务为 rollback only
                if(!toCommit && tm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    tm.getTransaction().setRollbackOnly();
                }
            }
            if(suspendedTrans != null) {
                tm.resume(suspendedTrans);
            }
        }
    }

    public static void main(String[] args) {

    }
}
