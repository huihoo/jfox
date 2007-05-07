/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dependent;

import java.lang.reflect.Field;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jfox.ejb3.EJBBucket;
import org.jfox.entity.EntityManagerFactoryBuilderImpl;
import org.jfox.framework.dependent.Dependence;
import org.jfox.framework.dependent.InjectionException;

/**
 * 注入 @PersistenceUnit
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FieldPersistenceUnitDependence implements Dependence {

    private EJBBucket bucket;
    private Field field;
    private PersistenceUnit pu;

    public FieldPersistenceUnitDependence(EJBBucket bucket, Field field, PersistenceUnit pu) {
        this.bucket = bucket;
        this.field = field;
        this.pu = pu;
    }

    public void inject(Object instance) throws InjectionException {
        EntityManagerFactory emf;
        String unitName = pu.unitName();
        if (unitName.trim().length() == 0) {
            emf = EntityManagerFactoryBuilderImpl.getDefaultEntityManagerFactory();
        }
        else {
            emf = EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName);
        }
        // 使用 field 反射注入
        try {
            field.setAccessible(true);
            field.set(instance, emf);
        }
        catch (Exception e) {
            throw new InjectionException("Failed to inject field " + field.getName() + " of @PersistenceContext " + bucket.getEJBName(), e);
        }
    }

    public static void main(String[] args) {

    }
}
