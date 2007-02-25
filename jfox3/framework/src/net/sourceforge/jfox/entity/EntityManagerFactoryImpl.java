package net.sourceforge.jfox.entity;

import java.util.Collections;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import net.sourceforge.jfox.entity.NamedSQLTemplate;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private String unitName;

    private EntityManagerFactoryBuilderImpl emFactoryBuilder = null;

    private StandardXAPoolDataSource dataSource = null;

    private boolean open = true;

    public EntityManagerFactoryImpl(String unitName, EntityManagerFactoryBuilderImpl emFactoryBuilder, StandardXAPoolDataSource dataSource) {
        this.unitName = unitName;
        this.emFactoryBuilder = emFactoryBuilder;
        this.dataSource = dataSource;
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

    public static void main(String[] args) {

    }
}
