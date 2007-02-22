package net.sourceforge.jfox.entity.dao;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

import net.sourceforge.jfox.entity.EntityManagerExt;
import net.sourceforge.jfox.entity.EntityObject;
import net.sourceforge.jfox.entity.QueryExt;

/**
 * DAOSupport，封装了 DAO 的基本操作
 * DAOSupport 注入了 EntityManager，默认使用 default unit name, 如果需要使用其它的 unit，可以另外注入，或者附在 getEntityManager 方法
 * DAO 应该都从 DAOSupport 继承，并且实现为 Stateless Local SessionBean
 *
 * DAO 可以脱离容器运行
 *
 * DAOSupport已经发布成了 Statless EJB，所以如果简单的DAO操作，可以直接使用DAOSupport，而不用提供 DAO 子类
 *
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
@Stateless
@Local
public abstract class DAOSupport implements DataAccessObject {


    /**
     * 返回 EntityManager，默认注入的是 default
     */
    protected abstract EntityManagerExt getEntityManager();

    public final QueryExt createNamedNativeQuery(String queryName) {
        return (QueryExt)getEntityManager().createNamedQuery(queryName);
    }

    public final QueryExt createNativeQuery(String sql) {
        return createNativeQuery(sql, EntityObject.class);
    }

    public final QueryExt createNativeQuery(String sql, Class<?> resultClass) {
        return (QueryExt)getEntityManager().createNativeQuery(sql, resultClass);
    }

    /**
     * PK 只保证唯一，不包含任何业务意义，比如：对于 ID 连续性的要求
     */
    public long nextPK() {
        return PKgen.getInstance().nextPK();
    }

    /**
     * 返回该 DAO 定义的 NamedQuery
     */
    public final NamedNativeQuery[] getNamedQueries() {
        if (this.getClass().isAnnotationPresent(NamedNativeQueries.class)) {
            NamedNativeQueries sqlTemplate = this.getClass().getAnnotation(NamedNativeQueries.class);
            return sqlTemplate.value();
        }
        else {
            return new NamedNativeQuery[0];
        }
    }

    /**
     * 根据 DataObject 接口生成 DataObject 实例
     *
     * @param dataObjectIntfClass data object interface class
     */
    public static <T> T newEntityObject(Class<T> dataObjectIntfClass) {
        return newEntityObject(dataObjectIntfClass, new HashMap<String,Object>());
    }

    /**
     * 使用数据 Map 生成动态代理 PO
     *
     * @param dataObjectInterfClass data object class
     * @param dataMap                   data dataMap
     */
    public static <T> T newEntityObject(final Class<T> dataObjectInterfClass, final Map<String, Object> dataMap) {
        return MapperEntity.newEntityObject(dataObjectInterfClass, dataMap);
    }

}
