/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dao;

import org.jfox.entity.QueryExt;

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
}
