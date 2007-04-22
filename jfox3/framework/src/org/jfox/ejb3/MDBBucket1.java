package org.jfox.ejb3;

import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.ejb.RemoveException;
import javax.ejb.TimerService;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.jfox.framework.component.Module;

/**
 * //TODO: MDBBucket
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MDBBucket1 extends SessionBucket implements PoolableObjectFactory {

    private EJBObjectId ejbObjectId;
    private MDBEJBContextImpl mdbContext = null;

    /**
     * cache EJB proxy stub, stateless EJB have only one stub
     */
    private EJBObject proxyStub = null;

    /**
     * cache EJB instances
     */
    private final GenericObjectPool pool = new GenericObjectPool(this);

    public MDBBucket1(EJBContainer container, Class<?> beanClass, Module module) {
        super(container, beanClass, module);

        injectClassDependents();
    }

    protected AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        if(mdbContext == null){
            mdbContext = new MDBEJBContextImpl(ejbObjectId, instance);
        }
        return mdbContext;
    }

    protected EJBObjectId createEJBObjectId() {
        if (ejbObjectId == null) {
            ejbObjectId = new EJBObjectId(getEJBName());
        }
        return ejbObjectId;
    }

    public AbstractEJBContext getEJBContext(EJBObjectId ejbObjectId) {
        try {
            EJBContextImpl ejbContext = (EJBContextImpl)pool.borrowObject();
            return ejbContext;
        }
        catch (Exception e) {
            throw new EJBException("Create EJBContext failed.", e);
        }
    }

    public boolean isStateless() {
        return false;
    }

    public boolean isWebService() {
        return false;
    }

    public void reuseEJBContext(AbstractEJBContext ejbContext) {
    }


    public void activateObject(Object obj) throws Exception {
    }

    public void destroyObject(Object obj) throws Exception {
    }

    public Object makeObject() throws Exception {
        return null;
    }

    public void passivateObject(Object obj) throws Exception {
    }

    public boolean validateObject(Object obj) {
        return true;
    }

    // EJBContext Implementation
    @SuppressWarnings({"deprecation"})
    public class MDBEJBContextImpl extends EJBContextImpl {

        public MDBEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

        public TimerService getTimerService() throws IllegalStateException {
            return null;
        }

        // SessionContext
        public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
            return (T)proxyStub;
        }

        // EJBObject & EJBLocalObject
        public void remove() throws RemoveException {
            try {
                destroyObject(getEJBInstance());
            }
            catch (Exception e) {
                String msg = "Remove EJB instance failed!";
                logger.warn(msg, e);
                throw new RemoveException(msg);
            }
        }
    }
}
