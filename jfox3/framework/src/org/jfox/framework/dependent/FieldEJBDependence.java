package org.jfox.framework.dependent;

import java.lang.reflect.Field;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.EJBObject;
import javax.naming.NamingException;

import org.jfox.ejb3.EJBBucket;
import org.jfox.ejb3.EJBContainer;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.SystemModule;
import org.apache.log4j.Logger;

/**
 * 注入 Field Level @EJB
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FieldEJBDependence implements Dependence {

    static Logger logger = Logger.getLogger(FieldEJBDependence.class);

    private ComponentContext componentContext;
    private Field field;


    public FieldEJBDependence(ComponentContext componentContext, Field field) {
        this.componentContext = componentContext;
        this.field = field;
    }

    public void inject(Object instance) throws InjectionException {
        EJB ejb = field.getAnnotation(EJB.class);
        String beanName = ejb.beanName().trim();
        String mappedName = ejb.mappedName().trim();
        Class beanInterface = ejb.beanInterface();

        EJBObject targetEJBObject; // resolve dependence
        Collection<EJBContainer> ejbContainers = componentContext.findComponentBySuper(EJBContainer.class, SystemModule.name);

        if (ejbContainers.isEmpty()) {
            logger.warn("@EJB will not be injected, no EJBCotaner deployed! " + ejb);
            return;
        }

        EJBContainer ejbContainer = (EJBContainer)ejbContainers.iterator().next();
        if (!beanName.equals("")) { // 分析 beanName
            EJBBucket bucket = ejbContainer.getEJBBucket(beanName);
            if (bucket == null) {
                throw new InjectionException("Could not find ejb with bean name: " + beanName);
            }
            else {
                targetEJBObject = bucket.createProxyStub();
            }
        }
        else if (mappedName.length() != 0) {
            try {
                Object obj = ejbContainer.getNamingContext().lookup(mappedName);
                if (!(obj instanceof EJBObject)) {
                    throw new InjectionException("MappedName " + mappedName + " is not a ejb, but " + obj.toString() + "!");
                }
                else {
                    targetEJBObject = (EJBObject)obj;
                }
            }
            catch (NamingException e) {
                throw new InjectionException("Failed to lookup " + mappedName);
            }
        }
        else { // 解析 beanInterface
            if (beanInterface.equals(Object.class)) {
                beanInterface = field.getType();
            }
            Collection<EJBBucket> buckets = ejbContainer.getEJBBucketByBeanInterface(beanInterface);
            if (buckets.isEmpty()) {
                throw new InjectionException("");
            }
            else if (buckets.size() != 1) {
                throw new InjectionException("");
            }
            else {
                targetEJBObject = buckets.iterator().next().createProxyStub();
            }
        }

        // 没有找到 @EJB 对象
        if (targetEJBObject == null) {
            throw new InjectionException("Failed to find the dependent EJBObject " + ejb);
        }

        // 使用 field 反射注入
        try {
            field.setAccessible(true);
            field.set(instance, targetEJBObject);
        }
        catch (Exception e) {
            throw new InjectionException("Failed to inject field " + field.getName() + " of Component " + componentContext.getComponentId(), e);
        }

    }

    public static void main(String[] args) {

    }
}
