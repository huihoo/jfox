package net.sourceforge.jfox.entity;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface EntityManagerExt extends EntityManager {

    Connection getConnection() throws SQLException;

}
