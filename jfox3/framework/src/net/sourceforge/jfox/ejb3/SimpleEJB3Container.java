package net.sourceforge.jfox.ejb3;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.SystemException;

import net.sourceforge.jfox.ejb3.invocation.InterceptorsEJBInvocationHandler;
import net.sourceforge.jfox.ejb3.invocation.ThreadContextEJBInvocationHandler;
import net.sourceforge.jfox.ejb3.invocation.TransactionEJBInvocationHandler;
import net.sourceforge.jfox.ejb3.naming.ContextAdapter;
import net.sourceforge.jfox.ejb3.naming.InitialContextFactoryImpl;
import net.sourceforge.jfox.ejb3.timer.EJBTimer;
import net.sourceforge.jfox.ejb3.transaction.JTATransactionManager;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.annotation.Constant;
import net.sourceforge.jfox.framework.component.ActiveComponent;
import net.sourceforge.jfox.framework.component.Component;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.ComponentUnregistration;
import net.sourceforge.jfox.framework.component.InstantiatedComponent;
import net.sourceforge.jfox.framework.component.InterceptableComponent;
import net.sourceforge.jfox.framework.component.Module;
import net.sourceforge.jfox.framework.component.ModuleListener;
import net.sourceforge.jfox.framework.event.ModuleEvent;
import net.sourceforge.jfox.framework.event.ModuleLoadingEvent;
import net.sourceforge.jfox.framework.event.ModuleUnloadedEvent;
import org.apache.log4j.Logger;

/**
 * 只支持 Local/Stateless Session Bean, Local MDB
 * 同时，该 Container 也承担了 NamingContainer 的能力
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Service(id = "EJB3Container", singleton = true, active = true, priority = -8)
public class SimpleEJB3Container implements EJBContainer, Component, InstantiatedComponent, InterceptableComponent, ModuleListener, ActiveComponent, ComponentUnregistration {

    protected Logger logger = Logger.getLogger(SimpleEJB3Container.class);

    // Transaction Manager
    private JTATransactionManager tm = null;

    @Constant(type=Integer.class, value = "$jta_transaction_timeout")
    private int transactionTimeout = 30; // default transaction timeout 30 seconds

    // TimerServer
    private EJBContainerTimerService timerService = null;

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


    public SimpleEJB3Container() {
        invocationChain.add(new ThreadContextEJBInvocationHandler());
        invocationChain.add(new TransactionEJBInvocationHandler());
        invocationChain.add(new InterceptorsEJBInvocationHandler());
    }

    public void instantiated(ComponentContext componentContext) {

    }

    public void postPropertiesSet() {
        // new NamingContext, then set to InitialContextFactoryImpl
        namingContext = new ContainerNamingContext();
        InitialContextFactoryImpl.setInitialContext(namingContext);

        tm = JTATransactionManager.getIntsance();
        timerService = new EJBContainerTimerService();

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
        catch(SystemException e) {
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
    }

    /**
     * 监听 Module 事件，根据 Module 的 load/unload 事件来加载其中的 EJB
     *
     * @param moduleEvent module event
     */
    public void moduleChanged(ModuleEvent moduleEvent) {
        if (moduleEvent instanceof ModuleLoadingEvent) {
            Module module = moduleEvent.getModule();
            EJBBucket[] buckets = loadEJB(module);
            for (EJBBucket bucket : buckets) {
                bucketMap.put(bucket.getName(), bucket);
            }
        }
        if (moduleEvent instanceof ModuleUnloadedEvent) {
            Module module = moduleEvent.getModule();
            unloadEJB(module);
        }
    }

    protected EJBBucket[] loadEJB(Module module) {
        Class[] statelessBeans = module.getModuleClassLoader().findClassAnnotatedWith(Stateless.class);
        List<EJBBucket> buckets = new ArrayList<EJBBucket>();
        for (Class beanClass : statelessBeans) {
            EJBBucket bucket = loadStatelessEJB(beanClass, module);
            buckets.add(bucket);
            // bind to jndi
            try {
                this.getNamingContext().bind(bucket.getMappedName(), bucket.getProxyStub());
            }
            catch (NamingException e) {
                throw new EJBException("bind " + bucket.getMappedName() + " failed!", e);
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
            StatelessEJBBucket bucket = new StatelessEJBBucket(this, beanClass, module);
            return bucket;
        }
        return null;
    }

    protected void unloadEJB(Module module) {
        Iterator<Map.Entry<String, EJBBucket>> it = bucketMap.entrySet().iterator();
        while (it.hasNext()) {
            EJBBucket bucket = ((Map.Entry<String, EJBBucket>)it.next()).getValue();
            if (bucket.getModule() == module) {
                it.remove();
                try {
                    this.getNamingContext().unbind(bucket.getMappedName());
                }
                catch (NamingException e) {
                    throw new EJBException("unbind ejb: " + bucket.getMappedName() + " failed!", e);
                }
            }
        }
    }

    public EJBBucket[] listBuckets() {
        return bucketMap.values().toArray(new EJBBucket[bucketMap.size()]);
    }

    public EJBBucket getEJBBucket(String name) {
        return bucketMap.get(name);
    }

    /**
     * 通过接口类取 EJBBucket
     *
     * @param interfaceClass bean interface
     */
    public EJBBucket[] getEJBBucketByBeanInterface(Class interfaceClass) {
        List<EJBBucket> buckets = new ArrayList<EJBBucket>();
        for (EJBBucket bucket : bucketMap.values()) {
            if (bucket.matchInterface(interfaceClass)) {
                buckets.add(bucket);
            }
        }
        return buckets.toArray(new EJBBucket[buckets.size()]);
    }

    /**
     * 构造 ejb invocation，并且获得 chain，然后发起调用
     *
     * @param name   ejb name
     * @param method ejb method, 已经解析成实体方法
     * @param params parameters
     * @throws Exception exception
     */
    public Object invokeEJB(String name, Method method, Object[] params) throws Exception {
        EJBBucket bucket = getEJBBucket(name);
        // get instance from bucket's pool
        Object ejbInstance = null;
        try {
            ejbInstance = bucket.newEJBInstance();
            EJBInvocation invocation = new EJBInvocation(bucket, ejbInstance, method, params);
            invocation.setTransactionManager(getTransactionManager());
            Iterator<EJBInvocationHandler> chain = invocationChain.iterator();
            return chain.next().invoke(invocation, chain);
        }
        finally {
            // reuse ejb instance
            if (ejbInstance != null) {
                bucket.reuseEJBInstance(ejbInstance);
            }
        }
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

    // ------------ NamingContainer ------

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
                else {
                    String currentEJBname = currentEJBInvocation.getEJBname();
                    if (name.equals(JAVA_COMP_ENV)) {
                        // EJBBucket extends Context
                        return getEJBBucket(currentEJBname).getENContext();
                    }
                    else {
                        EJBBucket bucket = getEJBBucket(currentEJBname);
                        return bucket.getENContext().lookup(name.substring(JAVA_COMP_ENV.length() + 1));
                    }
                }
            }

            if (bucketMap.containsKey(name)) {// 是 EJB，而且使用的是 name
                name = bucketMap.get(name).getMappedName();
            }
            if (!jndiMap.containsKey(name)) {
                throw new NameNotFoundException(name);
            }
            return jndiMap.get(name);
        }
    }

    // Container Timer Service
    public class EJBContainerTimerService implements TimerService {

        /**
         * EJBTimerTasks, use WeakHashMap, when EJBTimerTask un contained by java.util.Timer,
         * it will be automatic removed by GC
         * EJBTimerTask => timer hashCode
         */
        private Map<EJBTimer, String> timerTasks = new WeakHashMap<EJBTimer, String>();

        private ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(2);

        public EJBContainerTimerService() {

        }

        public Timer createTimer(final long duration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimer timer = new EJBTimer(info);
            ScheduledFuture future = scheduleService.schedule(timer, duration, TimeUnit.MILLISECONDS);
            timer.setFuture(future);
            timerTasks.put(timer, System.currentTimeMillis() + "");
            return timer;
        }

        public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            return null;
        }

        public Timer createTimer(final long initialDuration, final long intervalDuration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            return null;
        }

        public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            return null;
        }

        public Collection getTimers() throws IllegalStateException, EJBException {
            return timerTasks.keySet();
        }

        public void timeout(final EJBTimer timer) throws EJBException {
            try {
                for (Method timeoutMethod : timer.getTimeoutMethods()) {

                    invokeEJB(timer.getEJBName(), timeoutMethod, new Object[]{timer});
                }
            }
            catch (Exception e) {
                throw new EJBException("TimedObject callback exception.", e);
            }
        }

        public void stop() {
            scheduleService.shutdown();
        }
    }
}
