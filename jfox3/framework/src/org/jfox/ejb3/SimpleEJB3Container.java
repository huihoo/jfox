package org.jfox.ejb3;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.jfox.ejb3.event.EJBLoadedComponentEvent;
import org.jfox.ejb3.event.EJBUnloadedComponentEvent;
import org.jfox.ejb3.invocation.InterceptorsEJBInvocationHandler;
import org.jfox.ejb3.invocation.ThreadContextEJBInvocationHandler;
import org.jfox.ejb3.invocation.TransactionEJBInvocationHandler;
import org.jfox.ejb3.naming.ContextAdapter;
import org.jfox.ejb3.naming.InitialContextFactoryImpl;
import org.jfox.ejb3.timer.EJBTimerTask;
import org.jfox.ejb3.transaction.JTATransactionManager;
import org.jfox.ejb3.security.SecurityContext;
import org.jfox.framework.annotation.Constant;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInstantiation;
import org.jfox.framework.component.ComponentUnregistration;
import org.jfox.framework.component.InterceptableComponent;
import org.jfox.framework.component.Module;
import org.jfox.framework.component.ModuleListener;
import org.jfox.framework.event.ModuleEvent;
import org.jfox.framework.event.ModuleLoadingEvent;
import org.jfox.framework.event.ModuleUnloadedEvent;
import org.apache.log4j.Logger;

/**
 * 只支持 Local/Stateless Session Bean, Local MDB
 * 同时，该 Container 也承担了 NamingContainer 的能力
 * <p/>
 * 必须保证 SimpleEJB3Container 第一个加载，否则，无法监听到 ModuleEvent，而无法 load ejb
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "EJB3Container", singleton = true, active = true, priority = Integer.MIN_VALUE)
public class SimpleEJB3Container implements EJBContainer, Component, ComponentInstantiation, InterceptableComponent, ModuleListener, ActiveComponent, ComponentUnregistration {

    protected Logger logger = Logger.getLogger(SimpleEJB3Container.class);

    // Transaction Manager
    private JTATransactionManager tm = null;

    // default Transaction timeout
    @Constant(type = Integer.class, value = "$jta_transaction_timeout")
    private int transactionTimeout = 60; // default transaction timeout 60 seconds

    // TimerServer
    private ContainerTimerService timerService = null;

    // container naming context, also is initialcontext for IntialContextFactoryImpl
    private Context namingContext = null;

    /**
     * 执行 ejb invocation 的 chain
     */
    private final List<EJBInvocationHandler> invocationChain = new ArrayList<EJBInvocationHandler>();

    /**
     * ejb name => EJBBucket
     */
    private final Map<String, EJBBucket> bucketMap = new ConcurrentHashMap<String, EJBBucket>();

    /**
     * jndi Resource
     */
    private final Map<String, Object> jndiMap = new ConcurrentHashMap<String, Object>();

    private ComponentContext componentContext;

    public SimpleEJB3Container() {
        invocationChain.add(new ThreadContextEJBInvocationHandler());
        invocationChain.add(new TransactionEJBInvocationHandler());
        invocationChain.add(new InterceptorsEJBInvocationHandler());
    }

    public void postContruct(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    public void postPropertiesSet() {
        // new NamingContext, then set to InitialContextFactoryImpl
        namingContext = new ContainerNamingContext();
        InitialContextFactoryImpl.setInitialContext(namingContext);

        tm = JTATransactionManager.getIntsance();
        tm.setDefaultTransactionTimeout(getTransactionTimeout());
        timerService = new ContainerTimerService();

        // 将 TransactionManager 注册 java:/TransactionManager
        try {
            tm.setTransactionTimeout(getTransactionTimeout());
            getNamingContext().bind("java:/TransactionManager", tm);
            getNamingContext().bind("java:/UserTransaction", tm);
        }
        catch (NamingException e) {
            logger.fatal("Bind TransactionManager error.", e);
            System.exit(1);
        }
        catch (SystemException e) {
            logger.fatal("Failed to setTransactionTimeout!", e);
            System.exit(1);
        }
    }

    public void preUnregister(ComponentContext context) {
        tm.stop();
        timerService.stop();
        try {
            namingContext.close();
        }
        catch (NamingException e) {
            logger.warn("EJBContainer NamingContext close exception.", e);
        }
        jndiMap.clear();
    }

    public void postUnregister() {

    }

    public int getTransactionTimeout() {
        return transactionTimeout;
    }

    public void setTransactionTimeout(int transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
        if (tm != null) {
            tm.setDefaultTransactionTimeout(transactionTimeout);
        }
    }

    /**
     * 监听 Module 事件，根据 Module 的 load/unload 事件来加载其中的 EJB
     *
     * @param moduleEvent module event
     */
    public void moduleChanged(ModuleEvent moduleEvent) {
        Module module = moduleEvent.getModule();
        if (moduleEvent instanceof ModuleLoadingEvent) {
            // 监听 ModuleLoadingEvent，以保证能够在Action之前完成 EJB 装载，从而在 Action 实例化的时候，注入
            // 对于 SYSTEM_MODULE，见 SystemModule.start，做了特殊处理
            EJBBucket[] buckets = loadEJB(module);
            for (EJBBucket bucket : buckets) {
                bucketMap.put(bucket.getEJBName(), bucket);
            }
        }
        else if (moduleEvent instanceof ModuleUnloadedEvent) {
            unloadEJB(module);
        }
    }

    protected EJBBucket[] loadEJB(Module module) {
        List<EJBBucket> buckets = new ArrayList<EJBBucket>();

        Class[] statelessBeans = module.getModuleClassLoader().findClassAnnotatedWith(Stateless.class);
        for (Class beanClass : statelessBeans) {
            EJBBucket bucket = loadStatelessEJB(beanClass, module);
            buckets.add(bucket);
            //fireEvent, 以便XFire可以 register Endpoint
            componentContext.fireComponentEvent(new EJBLoadedComponentEvent(componentContext.getComponentId(), bucket));
            // bind to jndi
            try {
                for (String mappedName : bucket.getMappedNames()) {
                    this.getNamingContext().bind(mappedName, bucket.createProxyStub());
                }
            }
            catch (NamingException e) {
                throw new EJBException("bind " + bucket.getMappedNames() + " failed!", e);
            }
            logger.info("EJB loaded, bean class: " + beanClass.getName());
        }
        Class[] statefulBeans = module.getModuleClassLoader().findClassAnnotatedWith(Stateful.class);
        for (Class beanClass : statefulBeans) {
            EJBBucket bucket = loadStatefulEJB(beanClass, module);
            buckets.add(bucket);
            // bind to jndi
            try {
                for (String mappedName : bucket.getMappedNames()) {
                    this.getNamingContext().bind(mappedName, bucket.createProxyStub());
                }
            }
            catch (NamingException e) {
                throw new EJBException("Failed to bind EJB with name: " + Arrays.toString(bucket.getMappedNames()) + " !", e);
            }
            logger.info("EJB loaded, bean class: " + beanClass.getName());
        }

        return buckets.toArray(new EJBBucket[buckets.size()]);
    }

    /**
     * 装载一个 EJB
     *
     * @param beanClass ejb bean class
     * @param module    module
     * @throws EJBContainerException
     */
    protected EJBBucket loadStatelessEJB(Class<?> beanClass, Module module) {
        if (beanClass.isAnnotationPresent(Stateless.class)) {
            StatelessBucket bucket = new StatelessBucket(this, beanClass, module);
            return bucket;
        }
        return null;
    }

    protected EJBBucket loadStatefulEJB(Class<?> beanClass, Module module) {
        if (beanClass.isAnnotationPresent(Stateful.class)) {
            StatefulBucket bucket = new StatefulBucket(this, beanClass, module);
            return bucket;
        }
        return null;
    }

    protected void unloadEJB(Module module) {
        Iterator<Map.Entry<String, EJBBucket>> it = bucketMap.entrySet().iterator();
        while (it.hasNext()) {
            EJBBucket bucket = it.next().getValue();
            if (bucket.getModule() == module) {
                it.remove();
                //fireEvent, 以便XFire可以 unregister Endpoint
                componentContext.fireComponentEvent(new EJBUnloadedComponentEvent(componentContext.getComponentId(), bucket));
                // destroy ejb bucket
                logger.info("Unload EJB: " + bucket.getEJBName() + ", Module: " + bucket.getModule().getName());
                bucket.destroy();
                try {
                    for (String mappedName : bucket.getMappedNames()) {
                        this.getNamingContext().unbind(mappedName);
                    }

                }
                catch (NamingException e) {
                    throw new EJBException("unbind ejb: " + bucket.getMappedNames() + " failed!", e);
                }
            }
        }
    }

    public Collection<EJBBucket> listBuckets() {
        return Collections.unmodifiableCollection(bucketMap.values());
    }

    public EJBBucket getEJBBucket(String name) {
        return bucketMap.get(name);
    }

    /**
     * 通过接口类取 EJBBucket
     *
     * @param interfaceClass bean interface
     */
    public Collection<EJBBucket> getEJBBucketByBeanInterface(Class interfaceClass) {
        List<EJBBucket> buckets = new ArrayList<EJBBucket>();
        for (EJBBucket bucket : bucketMap.values()) {
            if (bucket.isBusinessInterface(interfaceClass)) {
                buckets.add(bucket);
            }
        }
        return Collections.unmodifiableCollection(buckets);
    }

    /**
     * 构造 ejb invocation，并且获得 chain，然后发起调用
     *
     * @param ejbObjectId     ejb object id
     * @param interfaceMethod ejb interfaceMethod, 已经解析成实体方法
     * @param params          parameters
     * @param securityContext security context
     * @throws Exception exception
     */
    public Object invokeEJB(EJBObjectId ejbObjectId, Method interfaceMethod, Object[] params, SecurityContext securityContext) throws Exception {

        EJBBucket bucket = getEJBBucket(ejbObjectId.getEJBName());
        // get instance from bucket's pool
        AbstractEJBContext ejbContext = null;
        try {
            ejbContext = bucket.getEJBContext(ejbObjectId);
            Method concreteMethod = bucket.getConcreteMethod(interfaceMethod);
            if (concreteMethod == null) {
                throw new NoSuchMethodException("Could not found Concrete Business Method for interface method: " + interfaceMethod.getName());
            }
            EJBInvocation invocation = new EJBInvocation(ejbObjectId, bucket, ejbContext.getEJBInstance(), interfaceMethod, concreteMethod, params, securityContext);
            return invokeEJBInvocation(invocation);
        }
        finally {
            // reuse ejb instance
            if (ejbContext != null) {
                bucket.reuseEJBContext(ejbContext);
            }
        }

    }
    
    /**
     * invoke timeout method
     *
     * @param ejbObjectId     ejb object id
     * @param interfaceMethod timeout interfaceMethod，可能是实体方法，也可能是 TimedObject 接口方法
     * @param params          parameters
     * @throws Exception exception
     */
    protected Object invokeTimeout(EJBObjectId ejbObjectId, Method interfaceMethod, Object[] params, SecurityContext securityContext) throws Exception {
        EJBBucket bucket = getEJBBucket(ejbObjectId.getEJBName());
        // get instance from bucket's pool
        AbstractEJBContext ejbContext = null;
        try {
            ejbContext = bucket.getEJBContext(ejbObjectId);
            EJBInvocation invocation = new EJBInvocation(ejbObjectId, bucket, ejbContext.getEJBInstance(), interfaceMethod, interfaceMethod, params, securityContext);
            return invokeEJBInvocation(invocation);
        }
        finally {
            // reuse ejb instance
            if (ejbContext != null) {
                bucket.reuseEJBContext(ejbContext);
            }
        }
    }

    protected Object invokeEJBInvocation(EJBInvocation invocation) throws Exception {
        invocation.setTransactionManager(getTransactionManager());
        Iterator<EJBInvocationHandler> chain = invocationChain.iterator();
        return chain.next().invoke(invocation, chain);
    }

    public TransactionManager getTransactionManager() {
        return tm;
    }

    public TimerService getTimerService() {
        return timerService;
    }

    public Context getNamingContext() {
        return namingContext;
    }

    public boolean preInvoke(Method method, Object[] params) {
        return true;
    }

    public Object postInvoke(Method method, Object[] params, Object result, Throwable exception) {
        return result;
    }

    // ------------ JNDI Context ------

    public class ContainerNamingContext extends ContextAdapter {

        public void bind(String name, Object obj) throws NamingException {
            if (jndiMap.containsKey(name)) {
                throw new NameAlreadyBoundException(name);
            }
            jndiMap.put(name, obj);
        }

        public void rebind(String name, Object obj) throws NamingException {
            jndiMap.put(name, obj);
        }

        public void unbind(String name) throws NamingException {
            if (!jndiMap.containsKey(name)) {
                throw new NameNotFoundException(name);
            }
        }

        /**
         * 从 jndiMap lookup resource, ejb proxy stub 已经绑定了
         *
         * @param name resource or ejb name
         * @throws NamingException if name not found
         */
        public Object lookup(String name) throws NamingException {

            //解析 java:comp/env
            if (name.startsWith(JAVA_COMP_ENV)) {
                EJBInvocation currentEJBInvocation = EJBInvocation.current();
                if (currentEJBInvocation == null) {
                    // 不在 EJB 调用上下文中
                    throw new NameNotFoundException(JAVA_COMP_ENV);
                }

                if (name.equals(JAVA_COMP_ENV)) { // lookup java:comp/env
                    // EJBBucket extends Context
                    return getEJBBucket(currentEJBInvocation.getEJBObjectId().getEJBName()).getENContext(currentEJBInvocation.getEJBObjectId());
                }
                else { // lookup java:comp/env/abc
                    EJBBucket bucket = getEJBBucket(currentEJBInvocation.getEJBObjectId().getEJBName());
                    return bucket.getENContext(currentEJBInvocation.getEJBObjectId()).lookup(name.substring(JAVA_COMP_ENV.length() + 1));
                }
            }

            if (!jndiMap.containsKey(name)) {
                throw new NameNotFoundException(name);
            }
            return jndiMap.get(name);
        }

        public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
            final NamingEnumeration<Binding> bindings = listBindings(name);
            return new NamingEnumeration<NameClassPair>() {
                public NameClassPair next() throws NamingException {
                    return bindings.next();
                }

                public boolean hasMore() throws NamingException {
                    return bindings.hasMore();
                }

                public void close() throws NamingException {
                    bindings.close();
                }

                public boolean hasMoreElements() {
                    return bindings.hasMoreElements();
                }

                public NameClassPair nextElement() {
                    return bindings.nextElement();
                }
            };
        }

        public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
            final Map<String, Object> namingMap = new HashMap<String, Object>();
            if (name == null || name.trim().length() == 0 || name.trim().equals("/")) { // all Bindings
                namingMap.putAll(jndiMap);
            }
            else {
                namingMap.put(name, jndiMap.get(name));
            }
            final Iterator<Map.Entry<String, Object>> iterator = namingMap.entrySet().iterator();
            return new NamingEnumeration<Binding>() {
                public boolean hasMore() throws NamingException {
                    return iterator.hasNext();
                }

                public Binding next() throws NamingException {
                    Map.Entry<String, Object> entry = iterator.next();
                    return new Binding(entry.getKey(), entry.getValue());
                }

                public void close() throws NamingException {
                    // do nothing
                }

                public boolean hasMoreElements() {
                    return iterator.hasNext();
                }

                public Binding nextElement() {
                    try {
                        return next();
                    }
                    catch (NamingException nException) {
                        throw new EJBException("NamingEnumeration.nextElement exception.", nException);
                    }
                }
            };
        }
    }

    // Container Timer Service
    public class ContainerTimerService implements TimerService {

        /**
         * EJBTimerTasks, use WeakHashMap, when EJBTimerTask un contained by java.util.Timer,
         * it will be automatic removed by GC
         * EJBTimerTask => timer hashCode
         */
//        private Map<EJBTimerTask, String> timerTasks = new WeakHashMap<EJBTimerTask, String>();

        private ScheduledThreadPoolExecutor scheduleService = new ScheduledThreadPoolExecutor(2);

        public ContainerTimerService() {

        }

        public Timer createTimer(final long duration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = new EJBTimerTask(this, info);
            ScheduledFuture future = scheduleService.schedule(timer, duration, TimeUnit.MILLISECONDS);
            timer.setFuture(future);
//            timerTasks.put(timer, System.currentTimeMillis() + "");
            return timer;
        }

        public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = new EJBTimerTask(this, info);
            ScheduledFuture future = scheduleService.schedule(timer, expiration.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            timer.setFuture(future);
//            timerTasks.put(timer, System.currentTimeMillis() + "");
            return timer;
        }

        public Timer createTimer(final long initialDuration, final long intervalDuration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = new EJBTimerTask(this, info);
            ScheduledFuture future = scheduleService.scheduleWithFixedDelay(timer, initialDuration, intervalDuration, TimeUnit.MILLISECONDS);
            timer.setFuture(future);
//            timerTasks.put(timer, System.currentTimeMillis() + "");
            return timer;
        }

        public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimerTask timer = new EJBTimerTask(this, info);
            ScheduledFuture future = scheduleService.scheduleWithFixedDelay(timer, initialExpiration.getTime() - System.currentTimeMillis(), intervalDuration, TimeUnit.MILLISECONDS);
            timer.setFuture(future);
//            timerTasks.put(timer, System.currentTimeMillis() + "");
            return timer;
        }

        public Collection getTimers() throws IllegalStateException, EJBException {
//            return Collections.unmodifiableCollection(timerTasks.keySet());
            return Arrays.asList(scheduleService.getQueue().toArray(new Runnable[scheduleService.getQueue().size()]));
        }

        /**
         * 执行 Timeout 方法，有 ScheduleService 调用 EJBTimerTask.run，EJBTimerTask回调该方法，
         * 通过容器来调用，以提供事务和执行 lifecycle 回调
         *
         * @param ejbTimerTask ejb TimerTask
         * @throws EJBException ejb exception when error
         */
        public void timeout(final EJBTimerTask ejbTimerTask) throws EJBException {
            Method timeMethod = null;
            try {
                for (Method _timeoutMethod : ejbTimerTask.getTimeoutMethods()) {
                    timeMethod = _timeoutMethod;
                    logger.info("Call Timeout method: " + _timeoutMethod + " of EJB: " + ejbTimerTask.getEJBObjectId());
                    // 这样会启动事务
                    invokeTimeout(ejbTimerTask.getEJBObjectId(), _timeoutMethod, new Object[]{ejbTimerTask}, ejbTimerTask.getSecurityContext());
                }
            }
            catch (Exception e) {
                logger.error("Call Timeout method " + timeMethod + " throw exception.", e);
                throw new EJBException("TimedObject callback exception.", e);
            }
        }

        public void stop() {
            scheduleService.shutdown();
        }
    }
}
