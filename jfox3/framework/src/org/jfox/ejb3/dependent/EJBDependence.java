/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.dependent;

import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.EJBObject;
import javax.naming.NamingException;

import org.jfox.ejb3.EJBBucket;
import org.jfox.framework.dependent.InjectionException;
import org.jfox.framework.dependent.Dependence;

/**
 * Class Level EJB Dependence
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJBDependence implements Dependence {

    protected EJBBucket bucket;
    protected EJB ejb;

    public EJBDependence(EJBBucket bucket, EJB ejb) {
        this.bucket = bucket;
        this.ejb = ejb;
    }


    public EJBBucket getBucket() {
        return bucket;
    }

    public EJB getEJBAnntation() {
        return ejb;
    }

    /**
     * class level, instance is null
     *
     * @param instance null
     * @throws InjectionException inject exception
     */
    public void inject(Object instance) throws InjectionException {

        EJBObject targetEJBObject = null; // resolved dependended ejbObject

        String name = ejb.name().trim();
        String beanName = ejb.beanName().trim();
        String mappedName = ejb.mappedName().trim();
        Class beanInterface = ejb.beanInterface();
        //inject @EJB class level, bind to java:comp/env
        if (!beanName.equals("")) { // 分析 beanName
            EJBBucket bucket = this.bucket.getEJBContainer().getEJBBucket(beanName);
            if (bucket == null) {
                throw new InjectionException("Could not find ejb with bean name: " + beanName);
            }
            else {
                targetEJBObject = bucket.createProxyStub();
            }
        }
        else if (!beanInterface.equals(Object.class)) { // 解析 beanInterface
            Collection<EJBBucket> buckets = this.bucket.getEJBContainer().getEJBBucketByBeanInterface(beanInterface);
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
        if (name.length() != 0) {
            try {
                // bind 到 java:comp/env
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                bucket.getENContext(null).bind(name, targetEJBObject);
            }
            catch (NamingException e) {
                throw new InjectionException("Failed to injection bind java:comp/env/" + name, e);
            }
        }
    }

    public static void main(String[] args) {

    }
}
