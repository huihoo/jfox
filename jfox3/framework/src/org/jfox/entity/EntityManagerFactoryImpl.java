/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import java.sql.Connection;
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
        if(dataSource != null) {
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
        return emFactoryBuilder.getNamedQuery(name);
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

    public static void main(String[] args) {

    }
}
