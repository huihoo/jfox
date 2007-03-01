package net.sourceforge.jfox.ejb3;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.Identity;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBObject;
import javax.ejb.EJBs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import net.sourceforge.jfox.ejb3.dependent.EJBDependence;
import net.sourceforge.jfox.ejb3.dependent.FieldEJBDependence;
import net.sourceforge.jfox.ejb3.dependent.FieldResourceDependence;
import net.sourceforge.jfox.ejb3.dependent.ResourceDependence;
import net.sourceforge.jfox.ejb3.naming.ContextAdapter;
import net.sourceforge.jfox.ejb3.timer.EJBTimer;
import net.sourceforge.jfox.entity.dependent.FieldPersistenceContextDependence;
import net.sourceforge.jfox.framework.component.Module;
import net.sourceforge.jfox.framework.component.ModuleClassLoader;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.util.AnnotationUtils;
import net.sourceforge.jfox.util.ClassUtils;
import net.sourceforge.jfox.util.MethodUtils;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * Container of Statless EJB，store all Meta data, and as EJB Factory
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@SuppressWarnings({"deprecation"})
public class StatelessEJBBucket implements EJBBucket, PoolableObjectFactory {

    private static final Logger logger = Logger.getLogger(StatelessEJBBucket.class);

    private Class beanClass;
    private Class[] beanInterfaces = null;
    private String name;
    private String mappedName;
    private String description;

    private EJBContainer container = null;

    private EJBContextImpl ejbContext;
    private EJBTimerService ejbTimerService;
    private Context envContext;

    /**
     * Module of EJB
     */
    private Module module;

    /**
     * cached methods，speed up to get Method when reflect
     * cached method is concrete method
     * hash => Method
     */
    private final Map<Long, Method> methodMap = new HashMap<Long, Method>();

    /**
     * cache EJB instances
     */
    private final GenericObjectPool pool = new GenericObjectPool();

    /**
     * cache EJB proxy stub, stateless EJB have only one stub
     */
    private EJBObject proxyStub = null;

    /**
     * class level AroundInvoke interceptor methods
     */
    private List<Method> classInterceptorMethods = new ArrayList<Method>();
    /**
     * Method level interceptors
     * ejb method  => interceptor methods
     */
    private Map<Method, List<Method>> methodInterceptorMethods = new HashMap<Method, List<Method>>();

    /**
     * stateless session bean 只有 PostConstruct & PreDestroy 有效
     */
    private List<Method> postConstructMethods = new ArrayList<Method>();
    private List<Method> preDestroyMethods = new ArrayList<Method>();

    /**
     * 类级别的依赖，描述在 Class 上
     */
    private List<EJBDependence> classEJBDependents = new ArrayList<EJBDependence>();
    private List<ResourceDependence> classResourceDependents = new ArrayList<ResourceDependence>();

    /**
     * Field级别的依赖，描述在 Field 上
     */
    private List<FieldEJBDependence> fieldEJBdependents = new ArrayList<FieldEJBDependence>();
    private List<FieldResourceDependence> fieldResourcedependents = new ArrayList<FieldResourceDependence>();

    /**
     * persistenceContext 依赖
     */
    private List<FieldPersistenceContextDependence> fieldPersistenceContextDependences = new ArrayList<FieldPersistenceContextDependence>();

    /**
     * Component env Map, 保存 java:comp/env 对象，只保存 Class level 的注入
     * Field Level 不做 env 保存
     */
    private Map<String, Object> envMap = new HashMap<String, Object>();

    /**
     * Web Service 发布接口
     */
    private Class webServiceEndpointInterface = null;

    /**
     * \@WebService Annotation
     */
    private WebService wsAnnotation = null;

    public StatelessEJBBucket(EJBContainer container, Class<?> beanClass, Module module) {
        this.container = container;
        this.module = module;
        this.beanClass = beanClass;

        //根据 Local/Remote 指定的beanInterface
        Set<Class> annotatedBeanInterfaces = new HashSet<Class>();
        if (beanClass.isAnnotationPresent(Remote.class)) {
            Remote remote = beanClass.getAnnotation(Remote.class);
            if (remote.value().length != 0) {
                annotatedBeanInterfaces.addAll(Arrays.asList(remote.value()));
            }
        }
        if (beanClass.isAnnotationPresent(Local.class)) {
            Local local = beanClass.getAnnotation(Local.class);
            if (local.value().length != 0) {
                annotatedBeanInterfaces.addAll(Arrays.asList(local.value()));
            }
        }
        if (annotatedBeanInterfaces.isEmpty()) {
            this.beanInterfaces = beanClass.getInterfaces();
        }
        else {
            this.beanInterfaces = annotatedBeanInterfaces.toArray(new Class[annotatedBeanInterfaces.size()]);
        }

        Stateless stateless = beanClass.getAnnotation(Stateless.class);
        String name = stateless.name();
        if (name.equals("")) {
            name = beanClass.getSimpleName();
        }
        setName(name);

        String mappedName = stateless.mappedName();
        if (mappedName.equals("")) {
            mappedName = name + "/" + (isRemote() ? "remote" : "local");
        }
        setMappedName(mappedName);

        setDescription(stateless.description());

        //parse @WebService, simple parse @WebService
        if (beanClass.isAnnotationPresent(WebService.class)) {
            wsAnnotation = beanClass.getAnnotation(WebService.class);
            String endpointInterfaceName = wsAnnotation.endpointInterface();
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
                logger.warn("Can not load endpoint interface: " + endpointInterfaceName + " annotated in EJB bean class: " + getBeanClass().getName());
            }
        }

        pool.setFactory(this);

        introspectMethods();
        introspectClassInterceptors();
        introspectLifecycle();

        introspectClassDependents();
        introspectFieldDependents();

        injectClassDependents();
    }

    protected void injectClassDependents() {
        //解析类级依赖
        for (EJBDependence ejbDependence : classEJBDependents) {
            try {
                ejbDependence.inject(null);
            }
            catch (InjectionException e) {
                throw new EJBException("@EJB inject failed.", e);
            }
        }

        for (ResourceDependence resourceDependence : classResourceDependents) {
            try {
                resourceDependence.inject(null);
            }
            catch (InjectionException e) {
                throw new EJBException("@Resource inject failed.", e);
            }
        }
    }

    protected void introspectMethods() {
        // 缓存 EJB 方法，以便反射的时候，提升执行速度
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            cacheMethod(method);
        }
    }

    protected void cacheMethod(Method method) {
        long methodHash = MethodUtils.getMethodHash(method);
        methodMap.put(methodHash, method);
    }

    protected void introspectLifecycle() {
        Method[] _postConstructMethods = AnnotationUtils.getAnnotatedDeclaredMethods(this.getBeanClass(), PostConstruct.class);
        for (Method postConstructMethod : _postConstructMethods) {
            if (!Modifier.isAbstract(postConstructMethod.getModifiers())
                    && !Modifier.isStatic(postConstructMethod.getModifiers())
                    && postConstructMethod.getParameterTypes().length == 0) {
                postConstructMethods.add(postConstructMethod);
            }
            else {
                logger.warn("Invalid @PostConstruct method: " + postConstructMethod);
            }
        }

        Method[] _preDestroyMethods = AnnotationUtils.getAnnotatedDeclaredMethods(this.getBeanClass(), PreDestroy.class);
        for (Method preDestroyMethod : _preDestroyMethods) {
            if (!Modifier.isAbstract(preDestroyMethod.getModifiers())
                    && !Modifier.isStatic(preDestroyMethod.getModifiers())
                    && preDestroyMethod.getParameterTypes().length == 0) {
                preDestroyMethods.add(preDestroyMethod);
            }
            else {
                logger.warn("Invalid @preDestroy method: " + preDestroyMethod);
            }
        }
    }

    /**
     * 找到所有类级别的拦截方法
     */
    protected void introspectClassInterceptors() {
        // beanClass is in superClass array
        Class<?>[] superClasses = ClassUtils.getAllSuperclasses(getBeanClass());
        // 找出所有 Interceptors 类
        for (Class<?> clazz : superClasses) {
            Method[] aroundInvokeMethods = AnnotationUtils.getAnnotatedDeclaredMethods(clazz, AroundInvoke.class);
            if (aroundInvokeMethods.length > 0) {
                for (Method aroundInvokeMethod : aroundInvokeMethods) {
                    if (checkInterceptorMethod(clazz, aroundInvokeMethod)) {
                        // 还没有在classInterceptorMethods中，子类如果覆盖了父类的方法，父类的方法将不再执行
                        if (!classInterceptorMethods.contains(aroundInvokeMethod)) {
                            aroundInvokeMethod.setAccessible(true);
                            classInterceptorMethods.add(0,aroundInvokeMethod);
                        }
                    }
                }
            }
            //如果是 Bean Class 本身，则还需要发现方法级的 interceptor
            if (clazz.equals(getBeanClass())) {
                //为了简化， 只分析 Bean Class 的 class Interceptors
                Method[] interceptedBeanMethods = AnnotationUtils.getAnnotatedMethods(clazz, Interceptors.class);
                for (Method interceptedBeanMethod : interceptedBeanMethods) {
                    Interceptors interceptors = interceptedBeanMethod.getAnnotation(Interceptors.class);
                    Class[] interceptorClasses = interceptors.value();
                    // 取出 @AroundInvoke 方法
                    for (Class<?> interceptorClass : interceptorClasses) {
                        Method[] interceptorsAroundInvokeMethods = AnnotationUtils.getAnnotatedMethods(interceptorClass, AroundInvoke.class);
                        List<Method> validAroundInvokeMethods = new ArrayList<Method>();
                        for (Method aroundInvokeMethod : interceptorsAroundInvokeMethods) {
                            if (checkInterceptorMethod(clazz, aroundInvokeMethod)) {
                                validAroundInvokeMethods.add(0, aroundInvokeMethod);
                            }
                        }
                        methodInterceptorMethods.put(interceptedBeanMethod, validAroundInvokeMethods);
                    }
                }
                if (clazz.isAnnotationPresent(Interceptors.class)) {
                    Interceptors interceptors = clazz.getAnnotation(Interceptors.class);
                    Class[] interceptorClasses = interceptors.value();

                    // 取出 @AroundInvoke 方法
                    for (Class<?> interceptorClass : interceptorClasses) {
                        Method[] interceptorsAroundInvokeMethods = AnnotationUtils.getAnnotatedMethods(interceptorClass, AroundInvoke.class);
                        for (Method aroundInvokeMethod : interceptorsAroundInvokeMethods) {
                            if (checkInterceptorMethod(interceptorClass, aroundInvokeMethod)) {
                                // 还没有在classInterceptorMethods中，子类如果覆盖了父类的方法，父类的方法将不再执行
                                if (!classInterceptorMethods.contains(aroundInvokeMethod)) {
                                    aroundInvokeMethod.setAccessible(true);
                                    classInterceptorMethods.add(0, aroundInvokeMethod);
                                }
                            }
                        }
                    }
                }
                //TODO:检查 Interceptors 中的 PostConstruct PreDestroy
            }
        }
    }

    private boolean checkInterceptorMethod(Class<?> interceptorClass, Method aroundInvokeMethod) {
        // check aroundInvokeMethod 合法性
        if (!Modifier.isAbstract(aroundInvokeMethod.getModifiers()) // 非 abstract 方法
                && !Modifier.isStatic(aroundInvokeMethod.getModifiers()) // 非 static 方法
                && aroundInvokeMethod.getParameterTypes().length == 1 // 只有一个参数
                && aroundInvokeMethod.getParameterTypes()[0].equals(InvocationContext.class) // 参数类型为 InvocationContext
                && aroundInvokeMethod.getReturnType().equals(Object.class) // 返回 Object 类型
                && (!Modifier.isPrivate(aroundInvokeMethod.getModifiers()) // 不是 private 方法，或者是Bean自身的方法
                || interceptorClass.equals(getBeanClass()))
                ) {
            return true;
        }
        else {
            logger.warn("Invalid @AroundInvoke interceptor method: " + aroundInvokeMethod);
            return false;
        }
    }

    /**
     * 查找 Class Level Dependences
     */
    protected void introspectClassDependents() {
        if (this.getBeanClass().isAnnotationPresent(EJBs.class)) {
            EJB[] ejbs = this.getBeanClass().getAnnotation(EJBs.class).value();
            for (EJB ejb : ejbs) {
                classEJBDependents.add(new EJBDependence(this, ejb));
            }
        }
        if (this.getBeanClass().isAnnotationPresent(EJB.class)) {
            EJB ejb = this.getBeanClass().getAnnotation(EJB.class);
            classEJBDependents.add(new EJBDependence(this, ejb));
        }
        if (this.getBeanClass().isAnnotationPresent(Resources.class)) {
            Resource[] resources = this.getBeanClass().getAnnotation(Resources.class).value();
            for (Resource resource : resources) {
                classResourceDependents.add(new ResourceDependence(this, resource));
            }
        }
        if (this.getBeanClass().isAnnotationPresent(Resource.class)) {
            Resource resource = this.getBeanClass().getAnnotation(Resource.class);
            classResourceDependents.add(new ResourceDependence(this, resource));
        }
    }

    /**
     * 查找 Field Level Dependences
     */
    protected void introspectFieldDependents() {
        //需要发现 AllSuperClass
        List<Field> allEJBFields = new ArrayList<Field>();
        List<Field> allResourceFields = new ArrayList<Field>();
        List<Field> allPersistenceContextFields = new ArrayList<Field>();

        // getAllSuperClass，也包括了自已
        for (Class<?> clazz : ClassUtils.getAllSuperclasses(this.getBeanClass())) {
            Field[] ejbFields = AnnotationUtils.getAnnotatedFields(clazz, EJB.class);
            allEJBFields.addAll(Arrays.asList(ejbFields));

            Field[] resourceFields = AnnotationUtils.getAnnotatedFields(clazz, Resource.class);
            allResourceFields.addAll(Arrays.asList(resourceFields));

            Field[] persistenceContextFields = AnnotationUtils.getAnnotatedFields(clazz, PersistenceContext.class);
            allPersistenceContextFields.addAll(Arrays.asList(persistenceContextFields));
        }

        for (Field field : allEJBFields) {
            EJB ejb = field.getAnnotation(EJB.class);
            fieldEJBdependents.add(new FieldEJBDependence(this, field, ejb));
        }

        for (Field field : allResourceFields) {
            Resource resource = field.getAnnotation(Resource.class);
            fieldResourcedependents.add(new FieldResourceDependence(this, field, resource));
        }

        for (Field field : allPersistenceContextFields) {
            if (!EntityManager.class.isAssignableFrom(field.getType())) {
                throw new EJBException("@PersistenceContext must annotated on field with type " + EntityManager.class.getName() + ", " + field);
            }
            PersistenceContext pc = field.getAnnotation(PersistenceContext.class);
            fieldPersistenceContextDependences.add(new FieldPersistenceContextDependence(this, field, pc));
        }
    }

    protected Object instantiate() throws Exception {
        return pool.borrowObject();
    }

    public ModuleClassLoader getBucketClassLoader() {
        return this.module.getModuleClassLoader();
    }

    public Module getModule() {
        return module;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Class<?>[] getBeanInterfaces() {
        return beanInterfaces;
    }

    /**
     * 从 Pool 中得到一个新的 Bean 实例
     *
     * @throws Exception exception
     */
    public Object newEJBInstance() throws Exception {
        return pool.borrowObject();
    }

    /**
     * 将实例返回给 pool
     *
     * @param beanInstance
     * @throws Exception exception
     */
    public void reuseEJBInstance(Object beanInstance) throws Exception {
        pool.returnObject(beanInstance);
    }


    public EJBContext createEJBContext(Object instance) {
        if (ejbContext == null) {
            ejbContext = new EJBContextImpl();
        }
        return ejbContext;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getMappedName() {
        return mappedName;
    }

    protected void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    public EJBContainer getEJBContainer() {
        return container;
    }

    public Collection<Method> getClassInterceptorMethods() {
        return Collections.unmodifiableCollection(classInterceptorMethods);
    }

    public Collection<Method> getMethodInterceptorMethods(Method method) {
        if (methodInterceptorMethods.containsKey(method)) {
            return Collections.unmodifiableList(methodInterceptorMethods.get(method));
        }
        else {
            return Collections.emptyList();
        }
    }

    public boolean isRemote() {
        return !getBeanClass().isAnnotationPresent(Local.class);
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
                            if (method.getDeclaringClass().equals(EJBObject.class)) { // 拦截 EJBObject 方法
                                if (method.getName().equals("remove")) {
                                    return null;
                                }
                                else if (method.getName().equals("getEJBHome")) {
                                    // not support
                                    return null;
                                }
                                else if (method.getName().equals("getPrimaryKey")) {
                                    return -1L;
                                }
                                else if (method.getName().equals("getHandle")) {
                                    return new EJBHandleImpl(name);
                                }
                                else if (method.getName().equals("isIdentical")) {
                                    // 直接比较 toString
                                    return proxy.toString().equals(args[0].toString());
                                }
                                else {
                                    throw new EJBException(new NoSuchMethodException(method.getName()));
                                }
                            }
                            else if (method.getDeclaringClass().equals(Object.class)) { // 拦截 Object 方法
                                if (method.getName().equals("toString") && (args == null || args.length == 0)) {
                                    return "$proxy_ejb_stub{name=" + name + ",interface=" + Arrays.toString(getBeanInterfaces()) + "}";
                                }
                                else if (method.getName().equals("equals") && args != null && args.length == 1) {
                                    // 直接比较 toString
                                    return proxy.toString().equals(args[0].toString());
                                }
                                else if (method.getName().equals("hashCode") && (args == null || args.length == 0)) {
                                    return proxy.toString().hashCode();
                                }
                                else {
                                    // not support
                                    return null;
                                }
                            }
                            else { // 其它业务方法
                                return container.invokeEJB(name, getConcreteMethod(method), args);
                            }
                        }
                    }
            );
        }
        return proxyStub;
    }

    /**
     * 通过动态代理过来的接口方法，取得 Bean 实体方法，以便可以获得 Annotation
     *
     * @param method method
     */
    protected Method getConcreteMethod(Method method) {
        long methodHash = MethodUtils.getMethodHash(method);
        return methodMap.get(methodHash);
    }

    public boolean matchInterface(Class beanInterface) {
        for (Class bi : this.getBeanInterfaces()) {
            if (bi.equals(beanInterface)) {
                return true;
            }
        }
        return false;
    }

    //--- jakarta commons-pool PoolableObjectFactory ---
    public Object makeObject() throws Exception {
        Object obj = beanClass.newInstance();
        // post construct
        for (Method postConstructMethod : postConstructMethods) {
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
            preDestroyMethod.invoke(obj);
        }
    }

    public Context getENContext() {
        if (envContext == null) {
            envContext = new ENContext();
        }
        return envContext;
    }

    public class ENContext extends ContextAdapter {
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
    public class EJBContextImpl implements EJBContext {

        public Principal getCallerPrincipal() {
            return null;
        }

        public EJBHome getEJBHome() {
            return null;
        }

        public EJBLocalHome getEJBLocalHome() {
            return null;
        }

        public boolean getRollbackOnly() throws IllegalStateException {
            try {
                return getEJBContainer().getTransactionManager().getStatus() == Status.STATUS_MARKED_ROLLBACK;
            }
            catch (SystemException e) {
                throw new EJBException(e);
            }
        }

        public TimerService getTimerService() throws IllegalStateException {
            if (ejbTimerService == null) {
                ejbTimerService = new EJBTimerService();
            }
            return ejbTimerService;
        }

        public UserTransaction getUserTransaction() throws IllegalStateException {
            // CMT 不返回 UserTransaction
            return null;
        }

        public boolean isCallerInRole(final String roleName) {
            return false;
        }

        public Object lookup(final String name) {
            try {
                return getENContext().lookup(name);
            }
            catch (NamingException e) {
                logger.warn("EJBContext.lookup " + name + " failed.", e);
                return null;
            }
        }

        public void setRollbackOnly() throws IllegalStateException {
            try {
                getEJBContainer().getTransactionManager().setRollbackOnly();
            }
            catch (SystemException e) {
                throw new EJBException(e);
            }
        }

        @Deprecated
        public Identity getCallerIdentity() {
            return null;
        }

        @Deprecated
        public Properties getEnvironment() {
            return null;
        }

        @Deprecated
        public boolean isCallerInRole(final Identity role) {
            return false;
        }
    }

    // EJB TimerService
    public class EJBTimerService implements TimerService {

        public Timer createTimer(final long duration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimer timer = (EJBTimer)getEJBContainer().getTimerService().createTimer(duration, info);
            timer.setEJBName(getName());
            return timer;
        }

        public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimer timer = (EJBTimer)getEJBContainer().getTimerService().createTimer(expiration, info);
            timer.setEJBName(getName());
            return timer;
        }

        public Timer createTimer(final long initialDuration, final long intervalDuration, final Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimer timer = (EJBTimer)getEJBContainer().getTimerService().createTimer(initialDuration, intervalDuration, info);
            timer.setEJBName(getName());
            return timer;
        }

        public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
            EJBTimer timer = (EJBTimer)getEJBContainer().getTimerService().createTimer(initialExpiration, intervalDuration, info);
            timer.setEJBName(getName());
            return timer;
        }

        public Collection getTimers() throws IllegalStateException, EJBException {
            return getEJBContainer().getTimerService().getTimers();
        }

    }

    public static void main(String[] args) {

    }
}
