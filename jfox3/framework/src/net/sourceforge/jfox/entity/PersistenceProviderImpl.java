package net.sourceforge.jfox.entity;

import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class PersistenceProviderImpl implements PersistenceProvider {

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        throw new UnsupportedOperationException("PersistenceProvider.createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map)");
    }

    public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
        // 初始化 EntityManagerFactoryBuilderImpl
        return EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(emName);
    }

    public static void main(String[] args) {
        Persistence.createEntityManagerFactory("Default");
    }
}
