package net.sourceforge.jfox.ejb3.transaction;

import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.Status;
import javax.ejb.SessionSynchronization;
import javax.ejb.EJBException;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class EJBSynchronization implements Synchronization {
    
    private Transaction tx = null;
    private SessionSynchronization sessionSync = null;

    public EJBSynchronization(SessionSynchronization sessionSync, Transaction tx) {
        this.sessionSync = sessionSync;
        this.tx = tx;
    }

    public void afterBegin() {
        try {
            sessionSync.afterBegin();
        }
        catch(Exception e) {
            try {
                tx.setRollbackOnly();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            throw new EJBException("Unexpected error in afterBegin", e);
        }
    }

    public void afterCompletion(int status) {
        try {
            if(status == Status.STATUS_COMMITTED) {
                sessionSync.afterCompletion(true);
            }
            else {
                sessionSync.afterCompletion(false);
            }
        }
        catch(Exception e) {
            throw new EJBException("Unexpected error in afterCompletion", e);
        }
    }

    public void beforeCompletion() {
        try {
            sessionSync.beforeCompletion();
        }
        catch(Exception e) {
            try {
                tx.setRollbackOnly();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            throw new EJBException("Unexpected error in beforeCompletion", e);
        }
    }

}
