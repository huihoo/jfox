/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dao;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;

import org.jfox.entity.EntityFactory;
import org.jfox.entity.MappedEntity;
import org.jfox.entity.QueryExt;

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
     * 生成19的PK，比如：2006080816404856650
     * PK 只保证唯一，不包含任何业务意义，比如：对于 ID 连续性的要求
     */
    public long nextPK() {
        return PKGenerator.getInstance().nextPK();
    }

}
