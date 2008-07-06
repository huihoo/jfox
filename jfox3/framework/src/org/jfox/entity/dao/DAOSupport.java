/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dao;

import org.jfox.entity.MappedEntity;
import org.jfox.entity.QueryExt;
import org.jfox.entity.mapping.EntityFactory;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAOSupport，封装了 DAO 的基本操作
 * 需要由子类提供EntityManager，在 getEntityManager方法中返回
 *
 * DAO 应该都从 DAOSupport 继承，并且实现为 Stateless Local SessionBean
 *
 * DAO 可以脱离容器运行，此时不能使用@PersistenceContext注入，
 * 而只能通过javax.persistence.Persistence的静态方法来构造
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public abstract class DAOSupport implements DataAccessObject {

    /**
     * 返回 EntityManager，由子类使用 @PersistenceContext 注入
     */
    protected abstract EntityManager getEntityManager();

    /**
     * 根据 Entity Class生成 Entity对象
     *
     * @param entityClass entity class
     */
    public static <T> T newEntityObject(Class<T> entityClass) {
        return newEntityObject(entityClass, new HashMap<String,Object>());
    }

    /**
     * 使用数据 Map，生成 Entity对象，Map的key和Entity的Column对应
     *
     * @param entityClass entity class
     * @param dataMap data dataMap
     */
    public static <T> T newEntityObject(final Class<T> entityClass, final Map<String, Object> dataMap) {
        return EntityFactory.newEntityObject(entityClass, dataMap);
    }

    /**
     * 给定query name 创建 NamedNativeQeury
     * @param queryName query name
     */
    public QueryExt createNamedNativeQuery(String queryName) {
        return (QueryExt)getEntityManager().createNamedQuery(queryName);
    }

    /**
     * 给定 sql 构造 Qeury，Result类型为MappedEntity
     * @param sql native sql template
     */
    public QueryExt createNativeQuery(String sql) {
        return createNativeQuery(sql, MappedEntity.class);
    }

    /**
     * 给定 sql 构造 Qeury，Result类型为resultClass
     * @param sql native sql template
     * @param resultClass 返回的结果对象类型
     */
    public QueryExt createNativeQuery(String sql, Class<?> resultClass) {
        return (QueryExt)getEntityManager().createNativeQuery(sql, resultClass);
    }

    /**
     * 自动增加记录的 Query
     * @param entityClass entity class
     * @return QueryExt
     */
    public QueryExt createAutoInsertNativeQuery(Class entityClass) {
        return createNativeQuery(SQLGenerator.buildInsertSQL(entityClass), entityClass);
    }

    /**
     * 自动生成 Delete Query
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoDeleteByIdNativeQuery(Class entityClass) {
        return createNativeQuery(SQLGenerator.buildDeleteByIdSQL(entityClass), entityClass);
    }

    public QueryExt createAutoDeleteByColumnNativeQuery(Class entityClass, String... columns) {
        return createNativeQuery(SQLGenerator.buildDeleteByColumnSQL(entityClass, columns), entityClass);
    }

    /**
     * 自动生成 Update Query
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoUpdateNativeQuery(Class entityClass) {
        return createNativeQuery(SQLGenerator.buildUpdateSQL(entityClass), entityClass);
    }

    /**
     * 自动生成 Select all Query by id
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoSelectByIdNativeQuery(Class entityClass) {
        return createNativeQuery(SQLGenerator.buildSelectSQLById(entityClass), entityClass);
    }

    /**
     * 自动生成 Select all Query by specic column
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoSelectByColumnNativeQuery(Class entityClass, String... columns) {
        return createNativeQuery(SQLGenerator.buildSelectSQLByColumn(entityClass, columns), entityClass);
    }

    public QueryExt createAutoSelectInByIdNativeQuery(Class entityClass, List<Long> idList) {
        return createNativeQuery(SQLGenerator.buildSelectInSQLById(entityClass, idList), entityClass);
    }

    public QueryExt createAutoSelectInByColumnNativeQuery(Class entityClass, String columnName, List<String> columnValueList) {
        return createNativeQuery(SQLGenerator.buildSelectInSQLByColumn(entityClass, columnName, columnValueList), entityClass);
    }

    public QueryExt createAutoSelectbyCondition(Class entityClass, Condition condition){
        return createNativeQuery(SQLGenerator.buildSelectSQLByConditoin(entityClass, condition), entityClass);
    }

    /**
     * 生成19的PK，比如：2006080816404856650
     * PK 只保证唯一，不包含任何业务意义，比如：对于 ID 连续性的要求
     */
    public long nextPK() {
        return PKGenerator.getInstance().nextPK();
    }

}
