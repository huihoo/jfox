package net.sourceforge.jfox.ejb3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.security.Principal;
import java.security.Identity;
import java.rmi.RemoteException;
import javax.ejb.Timer;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.ejb.Remote;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.EJBs;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.EJBLocalObject;
import javax.ejb.SessionContext;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.TimerService;
import javax.ejb.Handle;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resources;
import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

import org.apache.log4j.Logger;
import net.sourceforge.jfox.framework.component.Module;
import net.sourceforge.jfox.framework.component.ModuleClassLoader;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.ejb3.interceptor.InterceptorMethod;
import net.sourceforge.jfox.ejb3.interceptor.InternalInterceptorMethod;
import net.sourceforge.jfox.ejb3.interceptor.ExternalInterceptorMethod;
import net.sourceforge.jfox.ejb3.dependent.EJBDependence;
import net.sourceforge.jfox.ejb3.dependent.ResourceDependence;
import net.sourceforge.jfox.ejb3.dependent.FieldEJBDependence;
import net.sourceforge.jfox.ejb3.dependent.FieldResourceDependence;
import net.sourceforge.jfox.ejb3.naming.ContextAdapter;
import net.sourceforge.jfox.entity.dependent.FieldPersistenceContextDependence;
import net.sourceforge.jfox.util.ClassUtils;
import net.sourceforge.jfox.util.MethodUtils;
import net.sourceforge.jfox.util.AnnotationUtils;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class SessionBucket implements EJBBucket {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private Class beanClass;
    private Class[] beanInterfaces = null;
    private String ejbName;

    private List<String> mappedNames = new ArrayList<String>(2);
    private String description;

    private EJBContainer container = null;

    private EJBContextImpl ejbContext;

    /**
     * Module of EJB
     */
    private Module module;

    /**
     * cached methods，speed up to get Method when reflect
     * cached method is concrete method
     * hash => Method
     */
    private final Map<Long, Method> concreteMethods = new HashMap<Long, Method>();

    private List<Method> timeoutMethods = new ArrayList<Method>();

    /**
     * class level @interceptor methods
     */
    private List<InterceptorMethod> classInterceptorMethods = new ArrayList<InterceptorMethod>();
    /**
     * Method level interceptors
     * ejb concrete method  => interceptor methods
     */
    private Map<Method, List<InterceptorMethod>> methodInterceptorMethods = new HashMap<Method, List<InterceptorMethod>>();

    /**
     * 在 Bean 实现类中的 @AroundInvoke
     */
    private List<InterceptorMethod> beanInterceptorMethods = new ArrayList<InterceptorMethod>();

    /**
     * stateless session bean 只有 PostConstruct & PreDestroy 有效
     */
    protected List<Method> postConstructMethods = new ArrayList<Method>();
    protected List<Method> preDestroyMethods = new ArrayList<Method>();

    /**
     * 类级别的依赖，描述在 Class 上
     */
    private List<EJBDependence> classEJBDependents = new ArrayList<EJBDependence>();
    private List<ResourceDependence> classResourceDependents = new ArrayList<ResourceDependence>();

    /**
     * Field级别的依赖，描述在 Field 上
     */
    protected List<FieldEJBDependence> fieldEJBdependents = new ArrayList<FieldEJBDependence>();
    protected List<FieldResourceDependence> fieldResourcedependents = new ArrayList<FieldResourceDependence>();

    /**
     * persistenceContext 依赖
     */
    protected List<FieldPersistenceContextDependence> fieldPersistenceContextDependences = new ArrayList<FieldPersistenceContextDependence>();

    public SessionBucket(EJBContainer container, Class<?> beanClass, Module module) {
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
            this.beanInterfaces = ClassUtils.getAllInterfaces(getBeanClass());
        }
        else {
            this.beanInterfaces = annotatedBeanInterfaces.toArray(new Class[annotatedBeanInterfaces.size()]);
        }

        Stateless stateless = beanClass.getAnnotation(Stateless.class);
        String name = stateless.name();
        if (name.equals("")) {
            name = beanClass.getSimpleName();
        }
        setEJBName(name);

        String mappedName = stateless.mappedName();
        if (mappedName.equals("")) {
            if (isRemote()) {
                addMappedName(name + "/remote");
            }
            if (isLocal()) {
                addMappedName(name + "/local");
            }
        }
        else {
            addMappedName(mappedName);
        }

        setDescription(stateless.description());

        introspectMethods();
        introspectLifecycleAndInterceptors();

        introspectClassDependents();
        introspectFieldDependents();

        injectClassDependents();
    }

    protected void introspectMethods() {
        // 缓存 EJB 方法，以便反射的时候，提升执行速度
        Set<Long> interfaceMethodHashes = new HashSet<Long>();
        for (Class<?> interfaceClass : getBeanInterfaces()) {
            for (Method method : interfaceClass.getMethods()) {
                long methodHash = MethodUtils.getMethodHash(method);
                interfaceMethodHashes.add(methodHash);
            }
        }

        Method[] concreteMethods = beanClass.getMethods();
        for (Method method : concreteMethods) {
            long methodHash = MethodUtils.getMethodHash(method);
            if (interfaceMethodHashes.contains(methodHash)) {
                this.concreteMethods.put(methodHash, method);
            }

        }
    }

    /**
     * 找到所有类级别的拦截方法
     */
    protected void introspectLifecycleAndInterceptors() {
        // beanClass is in superClass array
        Class<?>[] superClasses = ClassUtils.getAllSuperclasses(getBeanClass());

        List<Long> timeoutMethodHashes = new ArrayList<Long>();
        List<Long> postConstructMethodHashes = new ArrayList<Long>();
        List<Long> preDestoryMethodHashes = new ArrayList<Long>();
        List<Long> aroundInvokeMethodHashes = new ArrayList<Long>();
        for (Class<?> superClass : superClasses) {

            for (Method timeoutMethod : introspectTimeoutMethod(superClass)) {
                long methodHash = MethodUtils.getMethodHash(timeoutMethod);
                if (!timeoutMethodHashes.contains(methodHash)) {
                    timeoutMethods.add(0, timeoutMethod);
                    timeoutMethodHashes.add(methodHash);
                }
            }

            // PostConstruct
            for (Method postConstructMethod : introspectPostContstructMethod(superClass)) {
                long methodHash = MethodUtils.getMethodHash(postConstructMethod);
                if (!postConstructMethodHashes.contains(methodHash)) {
                    postConstructMethods.add(0, postConstructMethod);
                    postConstructMethodHashes.add(methodHash);
                }
            }

            // PreDestroy
            for (Method preDestroyMethod : introspectPreDestroyMethod(superClass)) {
                long methodHash = MethodUtils.getMethodHash(preDestroyMethod);
                if (!preDestoryMethodHashes.contains(methodHash)) {
                    preDestroyMethods.add(0, preDestroyMethod);
                    preDestoryMethodHashes.add(methodHash);
                }
            }

            // @AroundInvoke
            for (Method aroundInvokeMethod : introspectAroundInvokeMethod(superClass)) {
                // 还没有在classInterceptorMethods中，子类如果覆盖了父类的方法，父类的方法将不再执行
                long methodHash = MethodUtils.getMethodHash(aroundInvokeMethod);
                if (!aroundInvokeMethodHashes.contains(methodHash)) {
                    beanInterceptorMethods.add(0, new InternalInterceptorMethod(aroundInvokeMethod));
                    aroundInvokeMethodHashes.add(methodHash);
                }
            }

            //如果是 Bean Class 本身，检查类级 @Interceptors
            if (superClass.equals(getBeanClass())) {
                // @Interceptors Method，取出所有可访问的 标准 @Interceptor 的方法
                Method[] interceptedBeanMethods = AnnotationUtils.getAnnotatedMethods(superClass, Interceptors.class);
                for (Method interceptedBeanMethod : interceptedBeanMethods) {
                    if (isBusinessMethod(interceptedBeanMethod)) { // 是业务方法
                        Interceptors interceptors = interceptedBeanMethod.getAnnotation(Interceptors.class);
                        Class[] interceptorClasses = interceptors.value();
                        // 取出 @AroundInvoke 方法
                        for (Class<?> interceptorClass : interceptorClasses) {
                            Method[] interceptorsAroundInvokeMethods = AnnotationUtils.getAnnotatedMethods(interceptorClass, AroundInvoke.class);
                            List<InterceptorMethod> validAroundInvokeMethods = new ArrayList<InterceptorMethod>();
                            for (Method aroundInvokeMethod : interceptorsAroundInvokeMethods) {
                                if (checkInterceptorMethod(superClass, aroundInvokeMethod)) {
                                    validAroundInvokeMethods.add(0, new ExternalInterceptorMethod(interceptorClass, aroundInvokeMethod));
                                }
                            }
                            methodInterceptorMethods.put(interceptedBeanMethod, validAroundInvokeMethods);
                        }
                    }
                }
                // @Interceptors Class， 为了简化， 只分析 Bean Class 上的Annotation
                if (superClass.isAnnotationPresent(Interceptors.class)) {
                    Interceptors interceptors = superClass.getAnnotation(Interceptors.class);
                    Class[] interceptorClasses = interceptors.value();

                    // 取出 @AroundInvoke 方法
                    for (Class<?> interceptorClass : interceptorClasses) {
                        Method[] interceptorsAroundInvokeMethods = AnnotationUtils.getAnnotatedMethods(interceptorClass, AroundInvoke.class);
                        for (Method aroundInvokeMethod : interceptorsAroundInvokeMethods) {
                            if (checkInterceptorMethod(interceptorClass, aroundInvokeMethod)) {
                                aroundInvokeMethod.setAccessible(true);
                                classInterceptorMethods.add(0, new ExternalInterceptorMethod(interceptorClass, aroundInvokeMethod));
                            }
                        }
                    }
                    //TODO: 检查 Interceptors 中的 PostConstruct PreDestroy
                }
            }
        }
    }

    protected List<Method> introspectTimeoutMethod(Class superClass) {
        List<Method> timeoutMethods = new ArrayList<Method>();
        Method[] annotatedTimeoutMethods = AnnotationUtils.getAnnotatedDeclaredMethods(superClass, Timeout.class);
        for (Method timeoutMethod : annotatedTimeoutMethods) {
            if (checkTimeoutMethod(superClass, timeoutMethod, Timeout.class)) {
                timeoutMethod.setAccessible(true);
                timeoutMethods.add(0, timeoutMethod);
            }
        }
        return timeoutMethods;
    }

    protected List<Method> introspectPostContstructMethod(Class superClass) {
        List<Method> postConstructMethods = new ArrayList<Method>();
        // PostConstruct
        Method[] _postConstructMethods = AnnotationUtils.getAnnotatedDeclaredMethods(superClass, PostConstruct.class);
        for (Method postConstructMethod : _postConstructMethods) {
            postConstructMethod.setAccessible(true);
            postConstructMethods.add(0, postConstructMethod);
        }
        return postConstructMethods;
    }

    protected List<Method> introspectPreDestroyMethod(Class superClass) {
        List<Method> preDestroyMethods = new ArrayList<Method>();
        Method[] _preDestroyMethods = AnnotationUtils.getAnnotatedDeclaredMethods(superClass, PreDestroy.class);
        for (Method preDestroyMethod : _preDestroyMethods) {
            if (checkCallbackMethod(superClass, preDestroyMethod, PreDestroy.class)) {
                preDestroyMethod.setAccessible(true);
                preDestroyMethods.add(0, preDestroyMethod);
            }
        }
        return preDestroyMethods;
    }

    protected List<Method> introspectAroundInvokeMethod(Class superClass) {
        List<Method> aroundInvokeMethods = new ArrayList<Method>();
        Method[] _aroundInvokeMethods = AnnotationUtils.getAnnotatedDeclaredMethods(superClass, AroundInvoke.class);
        if (_aroundInvokeMethods.length > 0) {
            for (Method aroundInvokeMethod : _aroundInvokeMethods) {
                if (checkInterceptorMethod(superClass, aroundInvokeMethod)) {
                    aroundInvokeMethod.setAccessible(true);
                    aroundInvokeMethods.add(0, aroundInvokeMethod);
                }
            }
        }
        return aroundInvokeMethods;
    }

    protected boolean isBusinessMethod(Method method) {
        return concreteMethods.containsKey(MethodUtils.getMethodHash(method));
    }

    protected boolean checkTimeoutMethod(Class<?> interceptorClass, Method timeoutMethod, Class<? extends Annotation> timeoutAnnotation) {
        if (!Modifier.isAbstract(timeoutMethod.getModifiers())
                && !Modifier.isStatic(timeoutMethod.getModifiers())
                && timeoutMethod.getParameterTypes().length == 1
                && timeoutMethod.getParameterTypes()[0].equals(Timer.class)) {
            return true;
        }
        else {
            logger.warn("Invalid @" + timeoutAnnotation.getSimpleName() + " method: " + timeoutMethod + " in class: " + interceptorClass);
            return false;
        }
    }

    protected boolean checkCallbackMethod(Class<?> interceptorClass, Method callbackMethod, Class<? extends Annotation> lifecyleAnnotation) {
        if (!Modifier.isAbstract(callbackMethod.getModifiers())
                && !Modifier.isStatic(callbackMethod.getModifiers())
                && callbackMethod.getParameterTypes().length == 0) {
            return true;
        }
        else {
            logger.warn("Invalid @" + lifecyleAnnotation.getSimpleName() + " method: " + callbackMethod + " in class: " + interceptorClass);
            return false;
        }
    }

    protected boolean checkInterceptorMethod(Class<?> interceptorClass, Method aroundInvokeMethod) {
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

    public String getEJBName() {
        return ejbName;
    }

    protected void setEJBName(String ejbName) {
        this.ejbName = ejbName;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String[] getMappedNames() {
        return mappedNames.toArray(new String[mappedNames.size()]);
    }

    protected void addMappedName(String mappedName) {
        mappedNames.add(mappedName);
    }

    public EJBContainer getEJBContainer() {
        return container;
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

    /**
     * 从 Pool 中得到一个新的 Bean 实例
     *
     * @param ejbObjectId ejb object id
     * @throws Exception exception
     */
    public abstract Object newEJBInstance(EJBObjectId ejbObjectId) throws Exception;

    /**
     * 将实例返回给 pool
     *
     * @param ejbId        ejb id
     * @param beanInstance ejb bean instance @throws Exception exception
     */
    public abstract void reuseEJBInstance(String ejbId, Object beanInstance) throws Exception;

    public EJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        if (ejbContext == null) {
            ejbContext = new EJBContextImpl(ejbObjectId, instance);
        }
        return ejbContext;
    }

    public Collection<InterceptorMethod> getClassInterceptorMethods() {
        return Collections.unmodifiableCollection(classInterceptorMethods);
    }

    public Collection<InterceptorMethod> getMethodInterceptorMethods(Method method) {
        if (methodInterceptorMethods.containsKey(method)) {
            return Collections.unmodifiableList(methodInterceptorMethods.get(method));
        }
        else {
            return Collections.emptyList();
        }
    }

    public Collection<InterceptorMethod> getBeanInterceptorMethods() {
        return Collections.unmodifiableCollection(beanInterceptorMethods);
    }

    public boolean isRemote() {
        // no @Local is Remote
        return getBeanClass().isAnnotationPresent(Remote.class) || !getBeanClass().isAnnotationPresent(Local.class);
    }

    public boolean isLocal() {
        return getBeanClass().isAnnotationPresent(Local.class);
    }

    protected Method[] getTimeoutMethods() {
        return timeoutMethods.toArray(new Method[timeoutMethods.size()]);
    }

    /**
     * destroy bucket, invoke when container unload ejb
     */
    public void destroy() {
        // do nothing
    }

    /**
     * 通过动态代理过来的接口方法，取得 Bean 实体方法，以便可以获得 Annotation
     *
     * @param interfaceMethod interfaceMethod
     */
    public Method getConcreteMethod(Method interfaceMethod) {
        long methodHash = MethodUtils.getMethodHash(interfaceMethod);
        return concreteMethods.get(methodHash);
    }

    public boolean isBusinessInterface(Class beanInterface) {
        for (Class bi : this.getBeanInterfaces()) {
            if (bi.equals(beanInterface)) {
                return true;
            }
        }
        return false;
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
    public class EJBContextImpl implements SessionContext, EJBObject, EJBLocalObject {

        private EJBObjectId ejbObjectId;
        private Object ejbInstance;

        public EJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            this.ejbObjectId = ejbObjectId;
            this.ejbInstance = ejbInstance;
        }

        protected EJBObjectId getEJBObjectId() {
            return ejbObjectId;
        }

        protected Object getEJBInstance() {
            return ejbInstance;
        }

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

        public UserTransaction getUserTransaction() throws IllegalStateException {
            //只支持 CMT, 不返回 UserTransaction
            return null;
        }

        public boolean isCallerInRole(final String roleName) {
            return false;
        }

        public Object lookup(final String name) {
            try {
                return getENContext(ejbInstance).lookup(name);
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

        // SessionContext
        public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
            throw new IllegalStateException("Can not invoke getBusinessObject!"); 
        }

        public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
            return this;
        }

        public EJBObject getEJBObject() throws IllegalStateException {
            return this;
        }

        public Class getInvokedBusinessInterface() throws IllegalStateException {
            return EJBInvocation.current().getInterfaceMethod().getDeclaringClass();
        }

        public MessageContext getMessageContext() throws IllegalStateException {
            return null;
        }

        public TimerService getTimerService() throws IllegalStateException {
            throw new IllegalStateException("Can not call getTimerService!");
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
            throw new RemoveException("Can not invoke remove()!");
        }

        public boolean isIdentical(EJBLocalObject obj) throws EJBException {
            return obj.getPrimaryKey().equals(getPrimaryKey());
        }

        // Object method
        public String toString() {
            return "ejb_stub{name=" + getEJBName() + ",interface=" + Arrays.toString(getBeanInterfaces()) + "}";
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof EJBObjectId)) {
                return false;
            }
            else {
                try {
                    return isIdentical((EJBObject)obj);
                }
                catch (Exception e) {
                    return false;
                }
            }
        }

        public int hashCode() {
            return super.hashCode();
        }

        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException(getEJBObjectId().toString());
        }
    }

}
