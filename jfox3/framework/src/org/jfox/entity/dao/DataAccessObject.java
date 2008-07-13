/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dao;

import org.jfox.entity.QueryExt;

import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface DataAccessObject {

    QueryExt createNamedNativeQuery(String queryName);

    /**
     * 返回类型为 EntityMapper
     * @param sql sql template
     */
    QueryExt createNativeQuery(String sql);

    QueryExt createNativeQuery(String sql, Class<?> resultClass);

    /**
     * 自动增加记录的 Query
     * @param entityClass entity class
     * @return QueryExt
     */
    QueryExt createAutoInsertNativeQuery(Class entityClass);

    /**
     * 自动生成 Delete Query
     * @param entityClass
     * @return QueryExt
     */
    QueryExt createAutoDeleteByIdNativeQuery(Class entityClass);

    QueryExt createAutoDeleteByColumnNativeQuery(Class entityClass, String... columns);

    /**
     * 自动生成 Update Query
     * @param entityClass
     * @return QueryExt
     */
    QueryExt createAutoUpdateNativeQuery(Class entityClass);

    /**
     * 自动生成 Select all Query by id
     * @param entityClass
     * @return QueryExt
     */
    QueryExt createAutoSelectByIdNativeQuery(Class entityClass);

    /**
     * 自动生成 Select all Query by specic column
     * @param entityClass
     * @return QueryExt
     */
    QueryExt createAutoSelectByColumnNativeQuery(Class entityClass, String... columns);


    QueryExt createAutoSelectInByIdNativeQuery(Class entityClass, List<Long> idList);

    QueryExt createAutoSelectInByColumnNativeQuery(Class entityClass, String columnName, List<String> columnValueList);

    QueryExt createAutoSelectByConditionNativeQuery(Class entityClass, Condition condition);

}
