package net.sourceforge.jfox.entity.dao;

import net.sourceforge.jfox.entity.QueryExt;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface DataAccessObject {

    QueryExt createNamedNativeQuery(String queryName);

    /**
     * 返回类型为 EntityMapper
     * @param sql sql template
     */
    QueryExt createNativeQuery(String sql);

    QueryExt createNativeQuery(String sql, Class<?> resultClass);
}
