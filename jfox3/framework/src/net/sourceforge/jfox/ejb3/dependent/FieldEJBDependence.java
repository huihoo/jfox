package net.sourceforge.jfox.ejb3.dependent;

import java.lang.reflect.Field;
import javax.ejb.EJB;
import javax.ejb.EJBObject;
import javax.naming.NamingException;

import net.sourceforge.jfox.ejb3.EJBBucket;
import net.sourceforge.jfox.framework.dependent.InjectionException;

/**
 * 注入 Field Level @EJB
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FieldEJBDependence extends EJBDependence {

    private Field field;

    public FieldEJBDependence(EJBBucket bucket, Field field, EJB ejb) {
        super(bucket, ejb);
        this.field = field;
    }

    public void inject(Object instance) throws InjectionException {
        String name = ejb.name().trim();
        String beanName = ejb.beanName().trim();
        String mappedName = ejb.mappedName().trim();
        Class beanInterface = ejb.beanInterface();

        EJBObject targetEJBObject = null; // resolve dependence

        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (name.length() != 0) {
            try {
                Object obj = getBucket().getENContext(null).lookup(name);
                if (!(obj instanceof EJBObject)) {
                    throw new InjectionException("Failed to inject field " + field.getName() + ", name " + name + " is not a ejb, but " + obj.toString() + "!");
                }
                else {
                    targetEJBObject = (EJBObject)obj;
                }
            }
            catch (NamingException e) {
                // ignore
            }
        }
        // 没有在 java:comp/env 中找到
        if (targetEJBObject == null) {
            if (!beanName.equals("")) { // 分析 beanName
                EJBBucket bucket = this.bucket.getEJBContainer().getEJBBucket(beanName);
                if (bucket == null) {
                    throw new InjectionException("Could not find ejb with bean name: " + beanName);
                }
                else {
                    targetEJBObject = bucket.createProxyStub();
                }
            }
            else if (mappedName.length() != 0) {
                try {
                    Object obj = this.bucket.getEJBContainer().getNamingContext().lookup(mappedName);
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
                if(beanInterface.equals(Object.class)) {
                    beanInterface = field.getType();
                }
                EJBBucket[] bucket = this.bucket.getEJBContainer().getEJBBucketByBeanInterface(beanInterface);
                if (bucket.length == 0) {
                    throw new InjectionException("Not found EJB by interface " + beanInterface.getName());
                }
                else if (bucket.length != 1) {
                    throw new InjectionException("Found more than on EJB bye interface " + beanInterface.getName());
                }
                else {
                    targetEJBObject = bucket[0].createProxyStub();
                }
            }

        }

        // 没有找到 @EJB 对象
        if (targetEJBObject == null) {
            throw new InjectionException("Failed to find the dependent EJBObject " + getEJBAnntation());
        }

        // 使用 field 反射注入
        try {
            field.setAccessible(true);
            field.set(instance, targetEJBObject);
        }
        catch (Exception e) {
            throw new InjectionException("Failed to inject field " + field.getName() + " of EJB " + bucket.getEJBName(), e);
        }
    }

    public static void main(String[] args) {

    }
}
