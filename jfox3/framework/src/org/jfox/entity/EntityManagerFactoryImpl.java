/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.jfox.entity.cache.CacheConfig;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

    Logger logger = Logger.getLogger(EntityManagerFactoryImpl.class);

    private String unitName;

    /**
     * jta-data-source in persistence.xml
     */
    private String jtaDataSource;

    private EntityManagerFactoryBuilderImpl emFactoryBuilder = null;

    private StandardXAPoolDataSource dataSource = null;

    private boolean open = true;

    /**
     * cache config map
     */
    private CacheConfig cacheConfig = null;

    private String databaseType = "Unknown";

    public EntityManagerFactoryImpl(String unitName, String jtaDataSource, EntityManagerFactoryBuilderImpl emFactoryBuilder, StandardXAPoolDataSource dataSource, CacheConfig cacheConfig) {
        this.unitName = unitName;
        this.jtaDataSource = jtaDataSource;
        this.emFactoryBuilder = emFactoryBuilder;
        this.dataSource = dataSource;
        this.cacheConfig = cacheConfig;
    }

    public EntityManagerFactoryBuilderImpl getEntityManagerFactoryBuilder() {
        return emFactoryBuilder;
    }

    public EntityManager createEntityManager() {
        return createEntityManager(Collections.emptyMap());
    }

    public EntityManager createEntityManager(final Map map) {
        return new EntityManagerImpl(this);
    }

    public boolean isOpen() {
        return (this.dataSource != null) && open;
    }

    public void close() {
        logger.info("Close EntityManagerFactory: " + getUnitName());
        // shutdown data source
        open = false;
        if (cacheConfig != null) {
            cacheConfig.close();
        }
        if (dataSource != null) {
            dataSource.shutdown(true);
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getJTADataSource() {
        return jtaDataSource;
    }

    public NamedSQLTemplate getNamedQuery(String name) {
        // 带数据库类型一起查找 NamedQuery
        return emFactoryBuilder.getNamedQuery(name, getDatabaseType());
    }

    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public String getDriver() {
        return ((StandardXADataSource)dataSource.getDataSource()).getDriverName();
    }

    public String getURL() {
        return ((StandardXADataSource)dataSource.getDataSource()).getUrl();
    }

    public String getUser() {
        return ((StandardXADataSource)dataSource.getDataSource()).getUser();
    }

    public String getPassword() {
        return ((StandardXADataSource)dataSource.getDataSource()).getPassword();
    }

    public int getMinSize() {
        return dataSource.getMinSize();
    }

    public int getMaxSize() {
        return dataSource.getMaxSize();
    }

    public long getLifeTime() {
        return dataSource.getLifeTime();
    }

    public long getSleepTime() {
        return dataSource.getSleepTime();
    }

    public long getDeadLockRetryWait() {
        return dataSource.getDeadLockRetryWait();
    }

    public long getDeadLockMaxWait() {
        return dataSource.getDeadLockMaxWait();
    }

    public int getCheckLevelObject() {
        return dataSource.getCheckLevelObject();
    }

    //TODO: 返回 Connection Meta，在Console上可以显示更多的信息
    public void checkConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    public void clearCache() {
        cacheConfig.clear();
    }

    /**
     * 得到数据类型，用来获取该类型的NamedNativeQeury SQL, 以支持多数据库
     */
    public String getDatabaseType() {
        if (databaseType == null) {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                DatabaseMetaData dbMeta = conn.getMetaData();
                String dbProductName = dbMeta.getDatabaseProductName().toUpperCase();
                if (dbProductName.indexOf("ORACLE") >= 0) {
                    databaseType = "Oracle";
                }
                else if (dbProductName.indexOf("MYSQL") >= 0) {
                    databaseType = "MySQL";
                }
                else if (dbProductName.indexOf("DB2") >= 0) {
                    databaseType = "DB2";
                }
                else if (dbProductName.indexOf("POSTGRESQL") >= 0) {
                    databaseType = "PostgreSQL";
                }
                else if (dbProductName.indexOf("MICROSOFT SQL SERVER") >= 0) {
                    databaseType = "SQLServer";
                }
                else if (dbProductName.indexOf("SYBASE") >= 0) {
                    databaseType = "Sybase";
                }
                else if (dbProductName.indexOf("INFORMIX") >= 0) {
                    databaseType = "Informix";
                }
                else if (dbProductName.indexOf("DERBY") >= 0) {
                    databaseType = "Derby";
                }
                else if (dbProductName.indexOf("HSQL") >= 0) {
                    databaseType = "HSQL";
                }
                else {
                    databaseType = "Unknown";
                }
            }
            catch (SQLException e) {
                logger.warn("Exception while getDatabaseType for DataSource " + dataSource, e);
            }
            finally {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (Exception e) {
                    logger.warn("Close connection failed after getDatabaseType for DataSource " + dataSource, e);
                }
            }
        }

        return databaseType;
    }

    public static void main(String[] args) {

    }
}
