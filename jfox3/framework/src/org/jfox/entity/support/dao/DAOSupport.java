/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.support.dao;

import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.EntityManagerFactoryBuilderImpl;
import org.jfox.entity.QueryExt;
import org.jfox.entity.mapping.EntityFactory;
import org.jfox.entity.support.idgen.TimebasedIdGenerator;
import org.jfox.entity.support.sqlgen.Condition;
import org.jfox.entity.support.sqlgen.SQLGenerator;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public static final String HINT_CACHE_PARTITION = EntityManagerFactoryBuilderImpl.QUERY_HINT_KEY_FOR_CACHE_PARTITION_NAME;
    public static final String HINT_JDBC_COMPATIBLE = EntityManagerFactoryBuilderImpl.QUERY_HINT_KEY_FOR_JDBC_COMPATIBLE;
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
/*
    public QueryExt createNativeQuery(String sql) {
        return createNativeQuery(sql, MappedEntity.class);
    }
*/

    /**
     * 给定 sql 构造 Qeury，Result类型为resultClass
     * @param sql native sql template
     * @param resultClass 返回的结果对象类型
     */
/*
    public QueryExt createNativeQuery(String sql, Class<?> resultClass) {
        return (QueryExt)getEntityManager().createNativeQuery(sql, resultClass);
    }
*/

    public QueryExt createTempNamedNativeQuery(String name, String sql, Class<?> resultClass, String partitionName){
        return (QueryExt)((EntityManagerExt)getEntityManager()).createTempNamedNativeQuery(name, sql, resultClass, partitionName);
    }

    /**
     * 自动增加记录的 Query
     * @param entityClass entity class
     * @return QueryExt
     */
    public QueryExt createAutoInsertNativeQuery(Class entityClass, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_INSERT_" + entityClass.getName(), SQLGenerator.buildInsertSQL(entityClass), entityClass, partitionName);
    }

    /**
     * 自动生成 Delete Query
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoDeleteByIdNativeQuery(Class entityClass, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_DELETE_" + entityClass.getName(), SQLGenerator.buildDeleteByIdSQL(entityClass), entityClass, partitionName);
    }

    public QueryExt createAutoDeleteByColumnNativeQuery(Class entityClass, String partitionName, String... columns) {
        return createTempNamedNativeQuery("AUTO_SQL_DELETE_" + entityClass.getName(), SQLGenerator.buildDeleteByColumnSQL(entityClass, columns), entityClass, partitionName);
    }

    /**
     * 自动生成 Update Query
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoUpdateNativeQuery(Class entityClass, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_UPDATE_" + entityClass.getName(), SQLGenerator.buildUpdateSQL(entityClass), entityClass, partitionName);
    }

    /**
     * 自动生成 Select all Query by id
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoSelectByIdNativeQuery(Class entityClass, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_SELECT_" + entityClass.getName(),SQLGenerator.buildSelectSQLById(entityClass), entityClass, partitionName);
    }

    /**
     * 自动生成 Select all Query by specic column
     * @param entityClass
     * @return QueryExt
     */
    public QueryExt createAutoSelectByColumnNativeQuery(Class entityClass, String partitionName, String... columns) {
        return createTempNamedNativeQuery("AUTO_SQL_SELECT_" + entityClass.getName(),SQLGenerator.buildSelectSQLByColumn(entityClass, columns), entityClass, partitionName);
    }

    public QueryExt createAutoSelectLikeByColumnNativeQuery(Class entityClass, String column, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_SELECT_" + entityClass.getName(),SQLGenerator.buildSelectLikeSQLByColumn(entityClass, column), entityClass, partitionName);
    }

    public QueryExt createAutoSelectInByIdNativeQuery(Class entityClass, List<Long> idList, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_SELECT_" + entityClass.getName(),SQLGenerator.buildSelectInSQLById(entityClass, idList), entityClass, partitionName);
    }

    public QueryExt createAutoSelectInByColumnNativeQuery(Class entityClass, String columnName, List<String> columnValueList, String partitionName) {
        return createTempNamedNativeQuery("AUTO_SQL_SELECT_" + entityClass.getName(),SQLGenerator.buildSelectInSQLByColumn(entityClass, columnName, columnValueList), entityClass, partitionName);
    }

    public QueryExt createAutoSelectByConditionNativeQuery(Class entityClass, Condition condition, String partitionName){
        return createTempNamedNativeQuery("AUTO_SQL_SELECT_" + entityClass.getName(),SQLGenerator.buildSelectSQLByConditoin(entityClass, condition), entityClass, partitionName);
    }


    /**
     * 根据 id 找到 Entity 对象
     *
     * @param namedQuery  named native sql
     * @param placeHolderName sql template column place holder name
     * @param id id
     * @return entity instance
     */
    public Object getEntityObjectByColumn(String namedQuery, String placeHolderName, Object id) {
        Map<String, Object> paramMap = new HashMap<String, Object>(1);
        paramMap.put(placeHolderName,id);
        List<?> entities = processNamedNativeQuery(namedQuery,paramMap);
        if(!entities.isEmpty()) {
            return entities.get(0);
        }
        else {
            return null;
        }
    }

    public int deleteEntityObjectByColumn(String namedQuery, String placeHolderName, Object id) {
        Map<String, Object> paramMap = new HashMap<String, Object>(1);
        paramMap.put(placeHolderName,id);
        return executeNamedNativeUpdate(namedQuery, paramMap);
    }

    public int executeNamedNativeUpdate(String namedQuery, Map<String, ?> paramMap) {
        Query query = createNamedNativeQuery(namedQuery);
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.executeUpdate();
    }

    /**
     * 使用一个预定义的 query 语句进行查询，返回 entity list
     *
     * @param namedQuery   named query
     */
    public List<?> processNamedNativeQuery(String namedQuery, Map<String, ?> paramMap) {
        return processNamedNativeQuery(namedQuery, paramMap, 0, Integer.MAX_VALUE);
    }

    /**
     * 使用一个预定义的 query 语句进行查询，返回 entity list
     *
     * @param namedQuery   named query
     * @param paramMap parameter map
     * @param firstResult 第一个值的位置
     * @param maxResult 取值范围
     * @return 返回符合需要的 entity list
     */
    public List<?> processNamedNativeQuery(String namedQuery, Map<String, ?> paramMap, int firstResult, int maxResult) {
        Query query = createNamedNativeQuery(namedQuery);
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResult);
        return (List<?>)query.getResultList();
    }

    public Object processNamedNativeQuerySingle(String namedQuery, Map<String, ?> paramMap){
        return processNamedNativeQuerySingle(namedQuery, paramMap, 0, Integer.MAX_VALUE);
    }

       /**
     * 使用一个预定义的 query 语句进行查询，返回 entity
     *
     * @param namedQuery   named query
     * @param paramMap parameter map
     * @param startPostion 第一个值的位置
     * @param maxResult 取值范围
     * @return 返回符合需要的 entity list
     */
    public Object processNamedNativeQuerySingle(String namedQuery, Map<String, ?> paramMap, int startPostion, int maxResult) {
        Query query = createNamedNativeQuery(namedQuery);
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        query.setFirstResult(startPostion);
        query.setMaxResults(maxResult);
        return query.getSingleResult();
    }

    public Object processNativeQuerySingle(Query query, Map<String, ?> paramMap){
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.getSingleResult();
    }

    public int executeNativeUpdate(Query query, Map<String, ?> paramMap) {
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.executeUpdate();
    }

    public List<?> processeNativeQuery(Query query, Map<String, ?> paramMap) {
        return processeNativeQuery(query, paramMap, 0 ,Integer.MAX_VALUE);
    }

    public List<?> processeNativeQuery(Query query, Map<String, ?> paramMap, int startPosition, int maxResult) {
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResult);
        return query.getResultList();
    }

    /**
     * 生成19的PK，比如：2006080816404856650
     * PK 只保证唯一，不包含任何业务意义，比如：对于 ID 连续性的要求
     */
    public long nextPK() {
        return TimebasedIdGenerator.getInstance().nextLongId();
    }

}
