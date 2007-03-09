package net.sourceforge.jfox.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import net.sourceforge.jfox.entity.cache.CacheConfig;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private String unitName;

    private EntityManagerFactoryBuilderImpl emFactoryBuilder = null;

    private StandardXAPoolDataSource dataSource = null;

    private boolean open = true;

    /**
     * cache config map
     */
    private Map<String, CacheConfig> cacheConfigMap = new HashMap<String, CacheConfig>();

    public EntityManagerFactoryImpl(String unitName, EntityManagerFactoryBuilderImpl emFactoryBuilder, StandardXAPoolDataSource dataSource, Map<String, CacheConfig> cacheConfigMap) {
        this.unitName = unitName;
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
        // shutdown data source
        open = false;
        dataSource.shutdown(true);
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public String getUnitName() {
        return unitName;
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

    public static void main(String[] args) {

    }
}
