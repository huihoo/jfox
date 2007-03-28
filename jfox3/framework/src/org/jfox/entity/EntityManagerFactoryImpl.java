package org.jfox.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jfox.entity.cache.CacheConfig;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.apache.log4j.Logger;

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
    private Map<String, CacheConfig> cacheConfigMap = new HashMap<String, CacheConfig>();

    public EntityManagerFactoryImpl(String unitName, String jtaDataSource, EntityManagerFactoryBuilderImpl emFactoryBuilder, StandardXAPoolDataSource dataSource, Map<String, CacheConfig> cacheConfigMap) {
        this.unitName = unitName;
        this.jtaDataSource = jtaDataSource;
        this.emFactoryBuilder = emFactoryBuilder;
        this.dataSource = dataSource;
        this.cacheConfigMap.putAll(cacheConfigMap);
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
        for(CacheConfig cacheConfig : cacheConfigMap.values()) {
            cacheConfig.close();
        }
        cacheConfigMap.clear();
        dataSource.shutdown(true);
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getJTADataSource(){
        return jtaDataSource;
    }

    public NamedSQLTemplate getNamedQuery(String name) {
        return emFactoryBuilder.getNamedQuery(name);
    }

    public void addCacheConfig(String name, CacheConfig cacheConfig) {
        cacheConfigMap.put(name,cacheConfig);
    }

    public CacheConfig getCacheConfig(String name) {
        return cacheConfigMap.get(name);
    }

    public Collection<CacheConfig> getCacheConfigs(){
        return Collections.unmodifiableCollection(cacheConfigMap.values());
    }

    public String getDriver(){
        return ((StandardXADataSource)dataSource.getDataSource()).getDriverName();
    }

    public String getURL(){
        return ((StandardXADataSource)dataSource.getDataSource()).getUrl();
    }

    public String getUser(){
        return ((StandardXADataSource)dataSource.getDataSource()).getUser();
    }

    public String getPassword(){
        return ((StandardXADataSource)dataSource.getDataSource()).getPassword();
    }

    public int getMinSize(){
        return dataSource.getMinSize();
    }

    public int getMaxSize(){
        return dataSource.getMaxSize();
    }

    public long getLifeTime(){
        return dataSource.getLifeTime();
    }

    public long getSleepTime(){
        return dataSource.getSleepTime();
    }

    public long getDeadLockRetryWait(){
        return dataSource.getDeadLockRetryWait();
    }

    public long getDeadLockMaxWait(){
        return dataSource.getDeadLockMaxWait();
    }

    public int getCheckLevelObject(){
        return dataSource.getCheckLevelObject();
    }

    public void checkConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    public void clearCache(){
        for(CacheConfig cacheConfig : cacheConfigMap.values()) {
            cacheConfig.clear();
        }
    }

    public static void main(String[] args) {

    }
}
