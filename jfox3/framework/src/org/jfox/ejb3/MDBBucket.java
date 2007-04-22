package org.jfox.ejb3;

import java.lang.reflect.Method;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.ejb.RemoveException;
import javax.ejb.TimerService;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.jfox.ejb3.dependent.FieldEJBDependence;
import org.jfox.ejb3.dependent.FieldResourceDependence;
import org.jfox.entity.dependent.FieldPersistenceContextDependence;
import org.jfox.framework.component.Module;

/**
 * Container of MessageDriven EJB，store all Meta data, and as EJB Factory
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MDBBucket extends SessionBucket implements PoolableObjectFactory {

    private EJBObjectId ejbObjectId;

    private MessageDrivenEJBContextImpl messageDrivenEJBContext;

    /**
     * cache EJB proxy stub, stateless EJB have only one stub
     */
    private EJBObject proxyStub = null;

    /**
     * cache EJB instances
     */
    private final GenericObjectPool pool = new GenericObjectPool(this);


    public MDBBucket(EJBContainer container, Class<?> beanClass, Module module) {
        super(container, beanClass, module);

        injectClassDependents();
    }


    /**
     * 从 Pool 中得到一个新的 Bean 实例
     *
     * @param ejbObjectId ejb object id
     * @throws javax.ejb.EJBException exception
     */
    public AbstractEJBContext getEJBContext(EJBObjectId ejbObjectId) throws EJBException {
        try {
            EJBContextImpl ejbContext = (EJBContextImpl)pool.borrowObject();
            return ejbContext;
        }
        catch (Exception e) {
            throw new EJBException("Create EJBContext failed.", e);
        }
    }

    /**
     * 将实例返回给 pool
     *
     * @param ejbContext ejb context
     */
    public void reuseEJBContext(AbstractEJBContext ejbContext) {
        try {
            pool.returnObject(ejbContext);
        }
        catch(Exception e){
            throw new EJBException("Return EJBContext to pool failed!", e);
        }
    }

    public AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        if (messageDrivenEJBContext == null) {
            messageDrivenEJBContext = new MessageDrivenEJBContextImpl(ejbObjectId, instance);
        }
        return messageDrivenEJBContext;
    }

    /**
     * 每个Stateless Bucket只有一个 EJBObjectId
     */
    public synchronized EJBObjectId createEJBObjectId() {
        if (ejbObjectId == null) {
            ejbObjectId = new EJBObjectId(getEJBName());
        }
        return ejbObjectId;
    }

    public boolean isStateless(){
        return false;
    }

    public boolean isWebService() {
        return false;
    }

    /**
     * destroy bucket, invoke when container unload ejb
     */
    public void destroy() {
        logger.debug("Destroy EJB: " + getEJBName() + ", Module: " + getModule().getName());
        try {
            pool.clear();
            pool.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 生成基于动态代理的 Stub
     */
    public synchronized EJBObject createProxyStub() {
        if (proxyStub == null) {
            proxyStub = super.createProxyStub();
        }
        return proxyStub;
    }

    //--- jakarta commons-pool PoolableObjectFactory ---
    public Object makeObject() throws Exception {
        Object obj = getBeanClass().newInstance();
        AbstractEJBContext ejbContext = createEJBContext(createEJBObjectId(), obj);
        // post construct
        for (Method postConstructMethod : getPostConstructMethods()) {
            logger.debug("PostConstruct method for ejb: " + getEJBName() + ", method: " + postConstructMethod);
            postConstructMethod.invoke(ejbContext.getEJBInstance());
        }

        // 注入 @EJB
        for (FieldEJBDependence fieldEJBDependence : fieldEJBdependents) {
            fieldEJBDependence.inject(ejbContext);
        }

        // 注入 @EJB
        for (FieldResourceDependence fieldResourceDependence : fieldResourcedependents) {
            fieldResourceDependence.inject(ejbContext);
        }

        // 注入 @PersistenceContext
        for (FieldPersistenceContextDependence fieldPersistenceContextDependence : fieldPersistenceContextDependences) {
            fieldPersistenceContextDependence.inject(ejbContext);
        }

        //返回 EJBContext
        return ejbContext;
    }

    public boolean validateObject(Object obj) {
        return true;
    }

    public void activateObject(Object obj) throws Exception {
    }

    public void passivateObject(Object obj) throws Exception {
    }

    public void destroyObject(Object obj) throws Exception {
        for (Method preDestroyMethod : getPreDestroyMethods()) {
            logger.debug("PreDestory method for ejb: " + getEJBName() + ", method: " + preDestroyMethod);
            preDestroyMethod.invoke(((AbstractEJBContext)obj).getEJBInstance());
        }
    }

    // EJBContext Implementation
    @SuppressWarnings({"deprecation"})
    public class MessageDrivenEJBContextImpl extends EJBContextImpl {

        public MessageDrivenEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
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
