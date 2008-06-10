/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.transaction;

import org.apache.log4j.Logger;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 通过 ThreadLocal 关联 Transaction，以及 Transaction 使用的 Connection，
 * 以便在 Transaction 结束的时候，释放connection，使得 connection 可以被 pool 回收
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TxConnectionsThreadLocal {

    private static Logger logger = Logger.getLogger(TxConnectionsThreadLocal.class);

    /**
     * ThreadLocal 保存 Thread => TransactionConnectionPair
     */
    private static ThreadLocal<TxConnectionWrapper> Thread2TxConn = new ThreadLocal<TxConnectionWrapper>();

    public static void setTransaction(Transaction transaction){
        logger.debug("Attach transaction to thread, transaction is: " + transaction);

        TxConnectionWrapper txConnectionWrapper = Thread2TxConn.get();
        if(txConnectionWrapper == null){ // 该线程还未发起新的事务
            txConnectionWrapper = new TxConnectionWrapper(transaction);
            Thread2TxConn.set(txConnectionWrapper);
        }
        else { // 该线程已经有事务，但又发起一个新的事务，新发起的事务接下来将是活动事务
            txConnectionWrapper.addTransaction(transaction);
        }
    }

    public static void addConnection2Tx(Connection connection) {
        logger.debug("Attatch connection to transaction, connection is: " + connection);
        TxConnectionWrapper txConnectionWrapper =  Thread2TxConn.get();

        if(txConnectionWrapper == null) {
            logger.warn("Attatch connection to transaction failed, no transaction!");
        }
        else {
            txConnectionWrapper.addConnection(connection);
        }
    }

    public static void releaseTxConnections(){
        logger.debug("Release Transaction attatched connections.");
        TxConnectionWrapper txConnectionWrapper = Thread2TxConn.get();
        if(txConnectionWrapper == null){
            logger.warn("Release attatched connections failed, current transaction is null.");
        }
        else {
            txConnectionWrapper.releaseCurrentTxConnections();
            // 如果没有剩余的事务环境，清除 ThreadLocal 变量
            if(txConnectionWrapper.isEmpty()) {
                logger.debug("Remove thread local transaction binding.");
                Thread2TxConn.remove();
            }
        }
    }

    /**
     * 保存和 Thread 关联的 Transaction 以及 Connection
     */
    static class TxConnectionWrapper {
        // 线程关联的事务链，最后一个事务即位活动事务
        List<Transaction> transactionChain = new ArrayList<Transaction>();

        // 线程关联的 TransactionConnectionPair
        List<TransactionConnectionPair> txconns = new ArrayList<TransactionConnectionPair>();

        public TxConnectionWrapper(Transaction transaction) {
            transactionChain.add(transaction);
        }

        public void addTransaction(Transaction transaction){
            transactionChain.add(transaction);
        }

        public void addConnection(Connection connection){
            // 当前事务即为活动事务
            //TODO: 如果 connection 还为加入过，才add
            txconns.add(new TransactionConnectionPair(transactionChain.get(transactionChain.size()-1),connection));
        }

        /**
         * 释放当前活动事务关联所有 connection
         */
        public void releaseCurrentTxConnections(){
            // 根据First Created, last commit 原则，活动事务为最后一个事务
            Transaction activeTransaction = transactionChain.remove(transactionChain.size()-1);

            try {
                if(activeTransaction.getStatus() != Status.STATUS_COMMITTED && activeTransaction.getStatus() != Status.STATUS_ROLLEDBACK ) {
                    logger.warn("Release connections attached non ended transaction!");
                }
            }
            catch(SystemException e) {
                logger.error("Get transaction status exception.",e );
            }

            Iterator<TransactionConnectionPair> it = txconns.iterator();
            while(it.hasNext()){
                TransactionConnectionPair tcp = it.next();
                if(tcp.transaction == activeTransaction) {
                    try {
                        it.remove();
                        tcp.connection.close();
                    }
                    catch(Throwable e) {
                        logger.warn("Connection close in release current transaction atteched connection with exception.",e);
                    }
                }
            }
        }

        public boolean isEmpty(){
            return transactionChain.isEmpty();
        }

        protected void finalize() throws Throwable {
            super.finalize();
            if(!txconns.isEmpty()){
                for(TransactionConnectionPair tcp : txconns){
                    try {
                        tcp.connection.close();
                    }
                    catch(Throwable e) {
                        logger.warn(" Connection close in finalize with exception.",e);
                    }
                }
            }
        }
    }

    static class TransactionConnectionPair {
        Transaction transaction;
        Connection connection;

        public TransactionConnectionPair(Transaction transaction, Connection connection) {
            this.transaction = transaction;
            this.connection = connection;
        }
    }

    public static void main(String[] args) {

    }
}
