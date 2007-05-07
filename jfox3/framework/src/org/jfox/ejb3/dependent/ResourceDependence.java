/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.dependent;

import javax.annotation.Resource;

import org.jfox.ejb3.EJBBucket;
import org.jfox.framework.dependent.InjectionException;
import org.jfox.framework.dependent.Dependence;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ResourceDependence implements Dependence {

    private EJBBucket bucket;
    private Resource resource;

    public ResourceDependence(EJBBucket bucket, Resource resource) {
        this.bucket = bucket;
        this.resource = resource;
    }


    public EJBBucket getBucket() {
        return bucket;
    }

    public Resource getResource() {
        return resource;
    }

    public void inject(Object instance) throws InjectionException {
    }

    public static void main(String[] args) {

    }
}
