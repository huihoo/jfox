package net.sourceforge.jfox.entity.dependent;

import java.lang.reflect.Field;
import javax.persistence.PersistenceContext;

import net.sourceforge.jfox.entity.EntityManagerExt;
import net.sourceforge.jfox.entity.EntityManagerFactoryBuilderImpl;
import net.sourceforge.jfox.framework.dependent.Dependence;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.ejb3.EJBBucket;

/**
 * 注入 @PersistenceContext
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FieldPersistenceContextDependence implements Dependence {

    private EJBBucket bucket;
    private Field field;
    private PersistenceContext pc;

    public FieldPersistenceContextDependence(EJBBucket bucket, Field field, PersistenceContext pc) {
        this.bucket = bucket;
        this.field = field;
        this.pc = pc;
    }

    public void inject(Object instance) throws InjectionException {
        EntityManagerExt em;
        String unitName = pc.unitName();
        if (unitName.trim().length() == 0) {
            em = (EntityManagerExt)EntityManagerFactoryBuilderImpl.getDefaultEntityManagerFactory();
        }
        else {
            em = (EntityManagerExt)EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName).createEntityManager();
        }
        // 使用 field 反射注入
        try {
            field.setAccessible(true);
            field.set(instance, em);
        }
        catch (Exception e) {
            throw new InjectionException("Failed to inject field " + field.getName() + " of @PersistenceContext " + bucket.getEJBName(), e);
        }
    }

    public static void main(String[] args) {

    }
}
