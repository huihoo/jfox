/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.EntityTransaction;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class EntityManagerExt implements EntityManager {

    public abstract String getUnitName();

    public abstract Connection getConnection() throws SQLException;

    public abstract EntityTransaction getTransaction();

    public abstract Query createNativeQuery(String sqlString, Class resultClass);

    public abstract Query createNativeQuery(String sqlString);

    public abstract void close();

    public void clear() {
        throw new UnsupportedOperationException("EntityManager.clear().");
    }

    public boolean contains(Object entity) {
        throw new UnsupportedOperationException("EntityManager.contains(Object entity).");
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
        throw new UnsupportedOperationException("EntityManager.flush, please use NamedQuery instead.");
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

    public boolean isOpen() {
        throw new UnsupportedOperationException("EntityManager.isOpen().");
    }

    public void joinTransaction() {
        throw new UnsupportedOperationException("EntityManager.joinTransaction().");
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


}
