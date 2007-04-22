package org.jfox.ejb3;

import org.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MDBBucket extends SessionBucket{
    //TODO: MDBBucket
    
    public MDBBucket(EJBContainer container, Class<?> beanClass, Module module) {
        super(container, beanClass, module);
    }

    protected AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        return null;
    }

    protected EJBObjectId createEJBObjectId() {
        return null;
    }

    public AbstractEJBContext getEJBContext(EJBObjectId ejbObjectId) {
        return null;
    }

    public boolean isStateless() {
        return false;
    }

    public boolean isWebService() {
        return false;
    }

    public void reuseEJBContext(AbstractEJBContext ejbContext) {
    }

    public static void main(String[] args) {

    }
}
