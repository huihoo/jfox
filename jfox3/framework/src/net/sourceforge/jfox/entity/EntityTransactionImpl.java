package net.sourceforge.jfox.entity;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.transaction.Status;
import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class EntityTransactionImpl implements EntityTransaction {

    private TransactionManager transactionManager = null;

    public EntityTransactionImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void begin() {
        try {
            transactionManager.begin();
        }
        catch(Exception e) {
            throw new PersistenceException("EntityTransaction.begin failed!", e);
        }
    }

    public void commit() {
        try {
            transactionManager.commit();
        }
        catch(Exception e) {
            throw new PersistenceException("EntityTransaction.commit failed!", e);
        }
    }

    public boolean getRollbackOnly() {
        try {
            return transactionManager.getStatus() == Status.STATUS_MARKED_ROLLBACK;
        }
        catch(Exception e) {
            throw new PersistenceException("EntityTransaction.isActive failed!", e);
        }
    }

    public boolean isActive() {
        try {
            return transactionManager.getStatus() == Status.STATUS_ACTIVE;
        }
        catch(Exception e) {
            throw new PersistenceException("EntityTransaction.isActive failed!", e);
        }
    }

    public void rollback() {
        try {
            transactionManager.rollback();
        }
        catch(Exception e) {
            throw new PersistenceException("EntityTransaction.rollback failed!", e);
        }
    }

    public void setRollbackOnly() {
        try {
            transactionManager.setRollbackOnly();
        }
        catch(Exception e) {
            throw new PersistenceException("EntityTransaction.setRollbackOnly failed!", e);
        }
    }

    public static void main(String[] args) {

    }
}
