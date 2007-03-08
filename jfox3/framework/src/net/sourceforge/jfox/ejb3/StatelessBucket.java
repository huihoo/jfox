package net.sourceforge.jfox.ejb3;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.ejb.RemoveException;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import net.sourceforge.jfox.ejb3.dependent.FieldEJBDependence;
import net.sourceforge.jfox.ejb3.dependent.FieldResourceDependence;
import net.sourceforge.jfox.ejb3.naming.ContextAdapter;
import net.sourceforge.jfox.ejb3.timer.EJBTimerTask;
import net.sourceforge.jfox.entity.dependent.FieldPersistenceContextDependence;
import net.sourceforge.jfox.framework.component.Module;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Container of Statless EJB，store all Meta data, and as EJB Factory
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class StatelessBucket extends SessionBucket implements PoolableObjectFactory {

    public static final Method TimeOut;
    static {
        try {
            TimeOut = TimedObject.class.getMethod("ejbTimeout", new Class[]{Timer.class});
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }
    }

    private EJBObjectId ejbObjectId;

    private StatelessEJBContextImpl statelessEJBContext;
    private EJBTimerService ejbTimerService;
    private Context envContext;

    /**
     * cache EJB proxy stub, stateless EJB have only one stub
     */
    private EJBObject proxyStub = null;

    /**
     * cache EJB instances
     */
    private final GenericObjectPool pool = new GenericObjectPool(this);

    /**
     * Web Service 发布接口
     */
    private Class webServiceEndpointInterface = null;

    /**
     * \@WebService Annotation
     */
    private WebService wsAnnotation = null;

    public StatelessBucket(EJBContainer container, Class<?> beanClass, Module module) {
        super(container,beanClass,module);

        //parse @WebService, simple parse @WebService
        if (beanClass.isAnnotationPresent(WebService.class)) {
            wsAnnotation = beanClass.getAnnotation(WebService.class);
            String endpointInterfaceName = wsAnnotation.endpointInterface();
            if (endpointInterfaceName == null || endpointInterfaceName.trim().length() == 0) {
                Class<?>[] beanInterfaces = this.getBeanInterfaces();
                if (beanInterfaces.length > 1) {
                    logger.warn("Use first Bean Interface " + beanInterfaces[0].getName() + " as endpoint interface.");

                }
                this.webServiceEndpointInterface = beanInterfaces[0];
            }
            else {
                try {
                    Class endpointInterface = this.getClass().getClassLoader().loadClass(endpointInterfaceName);
                    if (!endpointInterface.isInterface() || !Modifier.isPublic(endpointInterface.getModifiers())) {
                        logger.warn("Invalid endpoint interface: " + endpointInterface + " annotated in EJB bean class: " + getBeanClass().getName());
                    }
                    else {
                        this.webServiceEndpointInterface = endpointInterface;
                    }
                }
                catch (Exception e) {
                    logger.warn("Can not load endpoint interface: " + endpointInterfaceName + " annotated in EJB bean class: " + getBeanClass().getName(), e);
                }
            }
        }
    }

    protected void introspectMethods() {
        super.introspectMethods();

        // timeout method
        if(TimedObject.class.isAssignableFrom(getBeanClass())){
            timeoutMethods.add(TimeOut);
        }
    }

    /**
     * 从 Pool 中得到一个新的 Bean 实例
     *
     * @param ejbObjectId ejb object id
     * @throws Exception exception
     */
    public AbstractEJBContext newEJBContext(EJBObjectId ejbObjectId) throws Exception {
//        Object ejbInstance = pool.borrowObject();
//        createEJBContext(ejbObjectId, ejbInstance);
//        return ejbInstance;
        //TODO: 有问题，
        EJBContextImpl ejbContext = (EJBContextImpl)pool.borrowObject();
        return ejbContext;
    }

    /**
     * 将实例返回给 pool
     *
     * @param ejbContext
     */
    public void reuseEJBContext(AbstractEJBContext ejbContext) throws Exception {
        pool.returnObject(ejbContext);
    }

    public EJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        if (statelessEJBContext == null) {
            statelessEJBContext = new StatelessEJBContextImpl(ejbObjectId, instance);
        }
        return statelessEJBContext;
    }

    public synchronized EJBObjectId createEJBObjectId() {
        if(ejbObjectId == null) {
            ejbObjectId = new EJBObjectId(getEJBName());
        }
        return ejbObjectId;
    }

    public Class getWebServiceEndpointInterface() {
        //TOOD: 获取 webServiceEndpointInterface
        return webServiceEndpointInterface;
    }

    public WebService getWebServiceAnnotation() {
        return wsAnnotation;
    }

    /**
     * destroy bucket, invoke when container unload ejb
     */
    public void destroy() {
        // do nothing
    }

    /**
     * 生成基于动态代理的 Stub
     */
    public synchronized EJBObject getProxyStub() {
        if (proxyStub == null) {
            proxyStub = super.getProxyStub();
        }
        return proxyStub;
    }

    //--- jakarta commons-pool PoolableObjectFactory ---
    public Object makeObject() throws Exception {
        Object obj = getBeanClass().newInstance();
// post construct
        for (Method postConstructMethod : postConstructMethods) {
            logger.debug("PostConstruct method for ejb: " + getEJBName() + ", method: " + postConstructMethod);
            postConstructMethod.invoke(obj);
        }

        // 注入 @EJB
        for (FieldEJBDependence fieldEJBDependence : fieldEJBdependents) {
            fieldEJBDependence.inject(obj);
        }

        // 注入 @EJB
        for (FieldResourceDependence fieldResourceDependence : fieldResourcedependents) {
            fieldResourceDependence.inject(obj);
        }

        // 注入 @PersistenceContext
        for (FieldPersistenceContextDependence fieldPersistenceContextDependence : fieldPersistenceContextDependences) {
            fieldPersistenceContextDependence.inject(obj);
        }

        //TODO: 有问题， 返回 EJBContext
        return createEJBContext(createEJBObjectId(), obj);
    }

    public boolean validateObject(Object obj) {
        return true;
    }

    public void activateObject(Object obj) throws Exception {
    }

    public void passivateObject(Object obj) throws Exception {
    }

    public void destroyObject(Object obj) throws Exception {
        for (Method preDestroyMethod : preDestroyMethods) {
            logger.debug("PreDestory method for ejb: " + getEJBName() + ", method: " + preDestroyMethod);
            preDestroyMethod.invoke(obj);
        }
    }

    public Context getENContext(EJBObjectId ejbObjectId) {
        if (envContext == null) {
            envContext = new ENContext();
        }
        return envContext;
    }

    // EJBContext Implementation
    @SuppressWarnings({"deprecation"})
    public class StatelessEJBContextImpl extends EJBContextImpl{

        public StatelessEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

        public TimerService getTimerService() throws IllegalStateException {
            if (ejbTimerService == null) {
                ejbTimerService = new EJBTimerService();
            }
            return ejbTimerService;
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

    public class ENContext extends ContextAdapter {
        /**
         * Component env Map, 保存 java:comp/env 对象，只保存 Class level 的注入
         * Field Level 不做 env 保存
         */
        private Map<String, Object> envMap = new HashMap<String, Object>();

        //--- java:comp/env naming container
        public void bind(String name, Object obj) throws NamingException {
            if (envMap.containsKey(name)) {
                throw new NameAlreadyBoundException(name);
            }
            envMap.put(name, obj);
        }

        public void rebind(String name, Object obj) throws NamingException {
            envMap.put(name, obj);
        }

        public void unbind(String name) throws NamingException {
            if (!envMap.containsKey(name)) {
                throw new NameNotFoundException(name);
            }
            envMap.remove(name);
        }

        public Object lookup(String name) throws NamingException {
            if (!envMap.containsKey(name)) {
                throw new NameNotFoundException(name);
            }
            return envMap.get(name);
        }
    }

    // EJB TimerService，only stateless, MDB, Entity can register TimerService
    @SuppressWarnings("unchecked")
    public class EJBTimerService implements TimerService {

        public Timer createTimer(final long duration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(duration, info);
            timer.setEJBObjectId(createEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(expiration, info);
            timer.setEJBObjectId(createEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Timer createTimer(final long initialDuration, final long intervalDuration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(initialDuration, intervalDuration, info);
            timer.setEJBObjectId(createEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(initialExpiration, intervalDuration, info);
            timer.setEJBObjectId(createEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Collection getTimers() throws IllegalStateException, EJBException {
            List<EJBTimerTask> beanTimers = new ArrayList<EJBTimerTask>();
            for(EJBTimerTask timerTask :  (Collection<EJBTimerTask>)getEJBContainer().getTimerService().getTimers()){
                if(timerTask.getEJBObjectId().equals(createEJBObjectId())) {
                    beanTimers.add(timerTask);
                }
            }
            return Collections.unmodifiableCollection(beanTimers);
        }
    }

    public static void main(String[] args) {

    }
}
