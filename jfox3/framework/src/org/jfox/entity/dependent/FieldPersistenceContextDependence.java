/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dependent;

import java.lang.reflect.Field;
import javax.persistence.PersistenceContext;

import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.EntityManagerFactoryBuilderImpl;
import org.jfox.framework.dependent.Dependence;
import org.jfox.framework.dependent.InjectionException;
import org.jfox.ejb3.EJBBucket;
import org.jfox.ejb3.AbstractEJBContext;

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

    /**
     * 注入 PersistenceContext
     * 
     * @param ejbContext ejb context
     * @throws InjectionException injection exception
     */
    public void inject(Object ejbContext) throws InjectionException {
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
            field.set(((AbstractEJBContext)ejbContext).getEJBInstance(), em);
        }
        catch (Exception e) {
            throw new InjectionException("Failed to inject field " + field.getName() + " of @PersistenceContext " + bucket.getEJBName(), e);
        }
    }

    public static void main(String[] args) {

    }
}
