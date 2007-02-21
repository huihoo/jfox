package net.sourceforge.jfox.ejb3.dependent;

import javax.annotation.Resource;

import net.sourceforge.jfox.ejb3.EJBBucket;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.framework.dependent.Dependence;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
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
