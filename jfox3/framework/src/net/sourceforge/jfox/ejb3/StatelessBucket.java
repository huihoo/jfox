package net.sourceforge.jfox.ejb3;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.Handle;
import javax.ejb.RemoveException;
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
    private final GenericObjectPool pool = new GenericObjectPool();

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
        setEJBObjectId(new EJBObjectId(getEJBName()));

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

        pool.setFactory(this);
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
    public Object newEJBInstance(EJBObjectId ejbObjectId) throws Exception {
        Object ejbInstance = pool.borrowObject();
        createEJBContext(ejbObjectId, ejbInstance);
        return ejbInstance;
    }

    /**
     * 将实例返回给 pool
     *
     * @param ejbId        ejb id
     * @param beanInstance ejb bean instance @throws Exception exception
     */
    public void reuseEJBInstance(String ejbId, Object beanInstance) throws Exception {
        pool.returnObject(beanInstance);
    }

    public EJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        if (statelessEJBContext == null) {
            statelessEJBContext = new StatelessEJBContextImpl(ejbObjectId, instance);
        }
        return statelessEJBContext;
    }

    public EJBObjectId getEJBObjectId() {
        return ejbObjectId;
    }

    protected void setEJBObjectId(EJBObjectId ejbObjectId) {
        this.ejbObjectId = ejbObjectId;
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
            List<Class<?>> interfaces = new ArrayList<Class<?>>();
            interfaces.add(EJBObject.class);
            interfaces.addAll(Arrays.asList(this.getBeanInterfaces()));

            // 生成 EJB 的动态代理对象
            proxyStub = (EJBObject)Proxy.newProxyInstance(this.getModule().getModuleClassLoader(),
                    interfaces.toArray(new Class[interfaces.size()]),
                    new InvocationHandler() {
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            //需要判断是否是 EJBObject 的方法
                            if (method.getDeclaringClass().equals(EJBObject.class) || method.getDeclaringClass().equals(EJBLocalObject.class)) { // 拦截 EJBObject 方法
                                return method.invoke(statelessEJBContext, args);
                            }
                            //TODO: 优化处理 Object 方法
                            else if (method.getName().equals("toString") && (args == null || args.length == 0)) {
                                return statelessEJBContext.toString();
                            }
                            else if (method.getName().equals("equals") && args != null && args.length == 1) {
                                return statelessEJBContext.equals(args[0]);
                            }
                            else if (method.getName().equals("hashCode") && (args == null || args.length == 0)) {
                                return statelessEJBContext.hashCode();
                            }
                            else if (method.getName().equals("clone") && (args == null || args.length == 0)) {
                                return statelessEJBContext.clone();
                            }
                            else {
                                // 其它业务方法
                                return getEJBContainer().invokeEJB(ejbObjectId, method, args);
                            }
                        }
                    }
            );
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

        return obj;
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

    public Context getENContext(Object ejbInstance) {
        if (envContext == null) {
            envContext = new ENContext();
        }
        return envContext;
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

        public Handle getHandle() throws RemoteException {
            return new EJBHandleImpl(getEJBObjectId());
        }

        public Object getPrimaryKey() {
            return getEJBObjectId();
        }

        public boolean isIdentical(EJBObject obj) throws RemoteException {
            return obj.getPrimaryKey().equals(getPrimaryKey());
        }

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

    // EJB TimerService，only stateless, MDB, Entity can register TimerService
    @SuppressWarnings("unchecked")
    public class EJBTimerService implements TimerService {

        public Timer createTimer(final long duration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(duration, info);
            timer.setEJBObjectId(getEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(expiration, info);
            timer.setEJBObjectId(getEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Timer createTimer(final long initialDuration, final long intervalDuration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(initialDuration, intervalDuration, info);
            timer.setEJBObjectId(getEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = (EJBTimerTask)getEJBContainer().getTimerService().createTimer(initialExpiration, intervalDuration, info);
            timer.setEJBObjectId(getEJBObjectId());
            timer.addTimeoutMethod(getTimeoutMethods());
            return timer;
        }

        public Collection getTimers() throws IllegalStateException, EJBException {
            List<EJBTimerTask> beanTimers = new ArrayList<EJBTimerTask>();
            for(EJBTimerTask timerTask :  (Collection<EJBTimerTask>)getEJBContainer().getTimerService().getTimers()){
                if(timerTask.getEJBObjectId().equals(getEJBObjectId())) {
                    beanTimers.add(timerTask);
                }
            }
            return Collections.unmodifiableCollection(beanTimers);
        }
    }

    public static void main(String[] args) {

    }
}
