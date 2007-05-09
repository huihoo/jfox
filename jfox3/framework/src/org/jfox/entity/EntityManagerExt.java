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

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
//TODO: abstract EntityManagerExt
public interface EntityManagerExt extends EntityManager {

    Connection getConnection() throws SQLException;

}
