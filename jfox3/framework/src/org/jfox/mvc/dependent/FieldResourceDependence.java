package org.jfox.mvc.dependent;

import org.apache.log4j.Logger;
import org.jfox.ejb3.EJBBucket;
import org.jfox.ejb3.EJBContainer;
import org.jfox.framework.dependent.Dependence;
import org.jfox.framework.dependent.InjectionException;
import org.jfox.mvc.ActionBucket;

import javax.annotation.Resource;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jun 6, 2008 1:42:53 PM
 */
public class FieldResourceDependence implements Dependence {

    static Logger logger = Logger.getLogger(FieldResourceDependence.class);

    private ActionBucket actionBucket;
    private Field field;

    public FieldResourceDependence(ActionBucket actionBucket, Field field) {
        this.actionBucket = actionBucket;
        this.field = field;
    }

    public void inject(Object instance) throws InjectionException {
        Resource resource = field.getAnnotation(Resource.class);
        String mappedName = resource.mappedName().trim();
        Class beanInterface = resource.type();

        EJBObject targetResourceObject = null; // resolve dependence

        Collection<EJBContainer> ejbContainers = actionBucket.getModule().findComponentByInterface(EJBContainer.class);

        if (ejbContainers.isEmpty()) {
            logger.warn("@Resource will not be injected, no EJBCotaner deployed! " + resource);
            return;
        }

        EJBContainer ejbContainer = (EJBContainer)ejbContainers.iterator().next();
        if (!beanInterface.equals(Object.class)) { // 解析 beanInterface
            Collection<EJBBucket> buckets = ejbContainer.getEJBBucketByBeanInterface(beanInterface);
            if (buckets.isEmpty()) {
                throw new InjectionException("");
            }
            else if (buckets.size() != 1) {
                throw new InjectionException("");
            }
            else {
                targetResourceObject = buckets.iterator().next().createProxyStub();
            }
        }
        else if (mappedName.length() != 0) {
            try {
                Object obj = ejbContainer.getNamingContext().lookup(mappedName);
                if (!(obj instanceof EJBObject)) {
                    throw new InjectionException("MappedName " + mappedName + " is not a resource, but " + obj.toString() + "!");
                }
                else {
                    targetResourceObject = (EJBObject)obj;
                }
            }
            catch (NamingException e) {
                throw new InjectionException("Failed to lookup " + mappedName);
            }
        }

        // 没有找到 @EJB 对象
        if (targetResourceObject == null) {
            throw new InjectionException("Failed to find the dependent EJBObject " + resource);
        }

        // 使用 field 反射注入
        try {
            field.setAccessible(true);
            field.set(instance, targetResourceObject);
        }
        catch (Exception e) {
            throw new InjectionException("Failed to inject field " + field.getName() + " of Component " + actionBucket.getActionName(), e);
        }

    }


}
