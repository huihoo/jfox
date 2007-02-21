package net.sourceforge.jfox.framework.dependent;

import java.lang.reflect.Field;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.Component;
import net.sourceforge.jfox.framework.component.SystemModule;
import net.sourceforge.jfox.ejb3.EJBContainer;
import net.sourceforge.jfox.ejb3.EJBBucket;

/**
 * 注入 Field Level @EJB
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class FieldResourceDependence implements Dependence {

    static Logger logger = Logger.getLogger(FieldResourceDependence.class);

    private ComponentContext componentContext;
    private Field field;

    public FieldResourceDependence(ComponentContext componentContext, Field field) {
        this.componentContext = componentContext;
        this.field = field;
    }

    public void inject(Object instance) throws InjectionException {
        Resource resource = field.getAnnotation(Resource.class);
        String mappedName = resource.mappedName().trim();
        Class beanInterface = resource.type();

        EJBObject targetResourceObject = null; // resolve dependence

        Component[] ejbContainers = componentContext.findComponentBySuper(EJBContainer.class, SystemModule.name);

        if (ejbContainers.length == 0) {
            logger.warn("@Resource will not be injected, no EJBCotaner deployed! " + resource);
            return;
        }

        EJBContainer ejbContainer = (EJBContainer)ejbContainers[0];
        if (!beanInterface.equals(Object.class)) { // 解析 beanInterface
            EJBBucket[] bucket = ejbContainer.getEJBBucketByBeanInterface(beanInterface);
            if (bucket.length == 0) {
                throw new InjectionException("");
            }
            else if (bucket.length != 1) {
                throw new InjectionException("");
            }
            else {
                targetResourceObject = bucket[0].getProxyStub();
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
            throw new InjectionException("Failed to inject field " + field.getName() + " of Component " + componentContext.getComponentId(), e);
        }

    }

    public static void main(String[] args) {

    }
}
