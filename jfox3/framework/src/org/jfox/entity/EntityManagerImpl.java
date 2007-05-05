package org.jfox.entity;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.jfox.ejb3.transaction.TxConnectionsThreadLocal;

/**
 * Persistence Manager, 创建和管理 datasource, query template
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EntityManagerImpl implements EntityManagerExt {

//    private static final Logger logger = Logger.getLogger(EntityManagerImpl.class);

    private EntityManagerFactoryImpl emFactory = null;

    public EntityManagerImpl(EntityManagerFactoryImpl emFactory) {
        this.emFactory = emFactory;
    }

    protected DataSource getDataSource(){
        return emFactory.getDataSource();
    }

    public String getUnitName(){
        return emFactory.getUnitName();
    }

    public Connection getConnection() throws SQLException {
        //事务 commit 时 XAPool 不会自动 close 连接，
        //建立维护机制（TX Synchronization），在事务 commit 之后立即调用 connection.close，是 connection 回收到 Connection Pool
        DataSource ds = getDataSource();
        Connection conn = ds.getConnection();
        // add to threadlocal, 以便commit时, Tx sync 能够释放 connection,
        // 由 TransactionManager.begin时注册的 Synchronization 负责 release
        if(getTransaction().isActive()) {
            TxConnectionsThreadLocal.addConnection2Tx(conn);
        }
        return conn;
    }

    public QueryExt createNamedQuery(String name) {
        NamedSQLTemplate sqlTemplate = emFactory.getNamedQuery(name);
        // 没有对应的 SQLTemplate
        if(sqlTemplate == null) {
            throw new NamedQueryNotFoundException(name);
        }
        // 提供的参数不够，不能这么简单，传递的传输可能是个对象
/*
        if(sqlTemplate.getExpressions().length != args.length) {
            throw new NamedQueryArgumentException("Need " + sqlTemplate.getExpressions().length + " arguments for NamedQuery " + name + ", you supplied " + args.length);
        }
*/
        return new SQLQuery(this, sqlTemplate);
    }

    // javax.persistence.EntityManager

    public Query createNativeQuery(String sqlString) {
        return createNativeQuery(sqlString, MappedEntity.class);
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        SQLTemplate sqlTemplate = new SQLTemplate(sqlString,resultClass);
        return new SQLQuery(this,sqlTemplate);
    }

    public void clear() {
    }

    public void close() {
    }

    public boolean contains(Object entity) {
        return false;
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        throw new UnsupportedOperationException("EntityManager.createNamedQuery(String name), please use EntityManagerExt.createNativeQuery(String sqlString, Class resultClass) instead.");
    }

    public Query createQuery(String qlString) {
        throw new UnsupportedOperationException("EntityManager.createNamedQuery(String name), please use EntityManagerExt.createQuery(String name, Object... args) instead.");
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        throw new UnsupportedOperationException("EntityManager.find, please use NamedQuery instead.");
    }

    public void flush() {
    }

    public Object getDelegate() {
        throw new UnsupportedOperationException("EntityManager.getDelegate, please use NamedQuery instead.");
    }

    public FlushModeType getFlushMode() {
        throw new UnsupportedOperationException("EntityManager.getFlushMode, please use NamedQuery instead.");
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        throw new UnsupportedOperationException("EntityManager.getReference(Class<T> entityClass, Object primaryKey).");
    }

    public EntityTransaction getTransaction() {
        return emFactory.getEntityManagerFactoryBuilder().getEntityTransaction();
    }

    public boolean isOpen() {
        return true;
    }

    public void joinTransaction() {
    }

    public void lock(Object entity, LockModeType lockMode) {
        throw new UnsupportedOperationException("EntityManager.lock(Object entity, LockModeType lockMode).");
    }

    public <T> T merge(T entity) {
        throw new UnsupportedOperationException("EntityManager.merge(T entity).");
    }

    public void persist(Object entity) {
        throw new UnsupportedOperationException("EntityManager.persist(Object entity).");
    }

    public void refresh(Object entity) {
        throw new UnsupportedOperationException("EntityManager.refresh(Object entity).");
    }

    public void remove(Object entity) {
        throw new UnsupportedOperationException("EntityManager.remove(Object entity).");
    }

    public void setFlushMode(FlushModeType flushMode) {
        throw new UnsupportedOperationException("EntityManager.setFlushMode(FlushModeType flushMode).");
    }

    public static void main(String[] args) {

    }
}
