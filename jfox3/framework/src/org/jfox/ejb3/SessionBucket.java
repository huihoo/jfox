/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EJBs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Status;
import javax.transaction.SystemException;

import org.apache.log4j.Logger;
import org.jfox.ejb3.dependent.EJBDependence;
import org.jfox.ejb3.dependent.FieldEJBDependence;
import org.jfox.ejb3.dependent.FieldResourceDependence;
import org.jfox.ejb3.dependent.ResourceDependence;
import org.jfox.ejb3.interceptor.ExternalInterceptorMethod;
import org.jfox.ejb3.interceptor.InterceptorMethod;
import org.jfox.ejb3.interceptor.InternalInterceptorMethod;
import org.jfox.ejb3.naming.ContextAdapter;
import org.jfox.ejb3.security.SecurityContext;
import org.jfox.entity.dependent.FieldPersistenceContextDependence;
import org.jfox.entity.dependent.FieldPersistenceUnitDependence;
import org.jfox.framework.component.Module;
import org.jfox.framework.component.ModuleClassLoader;
import org.jfox.framework.dependent.InjectionException;
import org.jfox.mvc.SessionContext;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.ClassUtils;
import org.jfox.util.MethodUtils;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class SessionBucket implements EJBBucket {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private Class<?> beanClass;
    private Class[] ejbInterfaces = null;
    private String ejbName;

    private List<String> mappedNames = new ArrayList<String>(2);
    private String description = "";

    private EJBContainer container = null;

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
    protected List<FieldEJBDependence> fieldEJBdependents = new ArrayList<FieldEJBDependence>();
    protected List<FieldResourceDependence> fieldResourcedependents = new ArrayList<FieldResourceDependence>();

    /**
     * persistenceContext 依赖
     */
    protected List<FieldPersistenceContextDependence> fieldPersistenceContextDependences = new ArrayList<FieldPersistenceContextDependence>();
    protected List<FieldPersistenceUnitDependence> fieldPersistenceUnitDependences = new ArrayList<FieldPersistenceUnitDependence>();

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
            this.ejbInterfaces = ClassUtils.getAllInterfaces(getBeanClass());
        }
        else {
            this.ejbInterfaces = annotatedBeanInterfaces.toArray(new Class[annotatedBeanInterfaces.size()]);
        }

        introspectMethods();
        introspectLifecycleAndInterceptors();

        introspectClassDependents();
        introspectFieldDependents();
    }

    protected void introspectMethods() {
        // 缓存 EJB 方法，以便反射的时候，提升执行速度
        Set<Long> interfaceMethodHashes = new HashSet<Long>();
        for (Class<?> interfaceClass : getEJBInterfaces()) {
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

        List<Long> postConstructMethodHashes = new ArrayList<Long>();
        List<Long> preDestoryMethodHashes = new ArrayList<Long>();
        List<Long> aroundInvokeMethodHashes = new ArrayList<Long>();
        for (Class<?> superClass : superClasses) {

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
                    //TODO: 检测 Interceptors 中的 PostConstruct PreDestroy
                }
            }
        }
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

    protected Collection<Method> getPostConstructMethods() {
        return Collections.unmodifiableCollection(postConstructMethods);
    }

    protected Collection<Method> getPreDestroyMethods() {
        return Collections.unmodifiableCollection(preDestroyMethods);
    }

    protected boolean isBusinessMethod(Method method) {
        return concreteMethods.containsKey(MethodUtils.getMethodHash(method));
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
        List<Field> allPersistenceUnitFields = new ArrayList<Field>();
        
        // getAllSuperClass，也包括了自已
        for (Class<?> clazz : ClassUtils.getAllSuperclasses(this.getBeanClass())) {
            Field[] ejbFields = AnnotationUtils.getAnnotatedFields(clazz, EJB.class);
            allEJBFields.addAll(Arrays.asList(ejbFields));

            Field[] resourceFields = AnnotationUtils.getAnnotatedFields(clazz, Resource.class);
            allResourceFields.addAll(Arrays.asList(resourceFields));

            Field[] persistenceContextFields = AnnotationUtils.getAnnotatedFields(clazz, PersistenceContext.class);
            allPersistenceContextFields.addAll(Arrays.asList(persistenceContextFields));

            Field[] persistenceUnitFields = AnnotationUtils.getAnnotatedFields(clazz, PersistenceUnit.class);
            allPersistenceUnitFields.addAll(Arrays.asList(persistenceUnitFields));
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

        for (Field field : allPersistenceUnitFields) {
            if (!EntityManagerFactory.class.isAssignableFrom(field.getType())) {
                throw new EJBException("@PersistenceUnit must annotated on field with type " + EntityManagerFactory.class.getName() + ", " + field);
            }
            PersistenceUnit pu = field.getAnnotation(PersistenceUnit.class);
            fieldPersistenceUnitDependences.add(new FieldPersistenceUnitDependence(this, field, pu));
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

    public Class[] getEJBInterfaces() {
        return ejbInterfaces;
    }

    private String[] getEJBInterfaceNames() {
        Class[] interfaces = getEJBInterfaces();
        String[] interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaceNames[i] = interfaces[i].getName();
        }
        return interfaceNames;
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

    public Context getENContext(EJBObjectId ejbObjectId) {
        return getEJBContext(ejbObjectId).getENContext();
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
     * craete new EJBObjectId
     * <p/>
     * Stateless: only one EJBObjectId
     * Stateful: create new EJBObjectId very time to create new EJBContext
     */
    protected abstract EJBObjectId createEJBObjectId();

    /**
     * create a new EJBContext according ejbObjectId & instance
     *
     * @param ejbObjectId ejb object id
     * @param instance    ejb bean instance
     */
    protected abstract AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance);

    /**
     * get EJBContext according ejb object id
     *
     * @param ejbObjectId ejb object id
     * @throws EJBException exception
     */
    public abstract AbstractEJBContext getEJBContext(EJBObjectId ejbObjectId);

    /**
     * 将EJBContext返回给 pool, ejb context 中包含ejb instance
     *
     * @param ejbContext ejb context
     */
    public abstract void reuseEJBContext(AbstractEJBContext ejbContext);

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

    public boolean isSession() {
        return true;
    }

    public boolean isRemote() {
        // no @Local is Remote
        return getBeanClass().isAnnotationPresent(Remote.class) || !getBeanClass().isAnnotationPresent(Local.class);
    }

    public boolean isLocal() {
        return getBeanClass().isAnnotationPresent(Local.class);
    }


    public void start() {
        // do nothing
    }

    /**
     * destroy bucket, invoke when container unload ejb
     */
    public void stop() {
        // do nothing
    }

    public String toString() {
        return "EJB: " + getEJBName();
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
        for (Class bi : this.getEJBInterfaces()) {
            if (bi.equals(beanInterface)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成基于动态代理的 Stub
     */
    public synchronized EJBObject createProxyStub() {
        List<Class> interfaces = new ArrayList<Class>();
        interfaces.add(EJBObject.class);
        interfaces.addAll(Arrays.asList(this.getEJBInterfaces()));

        // 生成 EJB 的动态代理对象
        return (EJBObject)Proxy.newProxyInstance(this.getModule().getModuleClassLoader(),
                interfaces.toArray(new Class[interfaces.size()]),
                new ProxyStubInvocationHandler()
        );
    }

    class ProxyStubInvocationHandler implements InvocationHandler {
        EJBObjectId ejbObjectId = createEJBObjectId();

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //EJBObject 方法
            if (method.getDeclaringClass().equals(EJBObject.class) || method.getDeclaringClass().equals(EJBLocalObject.class)) {
                return method.invoke(getEJBContext(getEJBObjectId()), args);
            }
            else if (MethodUtils.isObjectMethod(method)) {
                //优化处理 Object 方法
                if (method.getName().equals("toString")) {
                    return "$ejb_proxy_stub{ejbid=" + ejbObjectId + ",interface=" + Arrays.toString(getEJBInterfaceNames()) + "}";
                }
                else if (method.getName().equals("equals")) {
                    return (args[0] instanceof ProxyStubInvocationHandler) &&  getEJBObjectId().equals(((ProxyStubInvocationHandler)args[0]).getEJBObjectId());
                }
                else if (method.getName().equals("hashCode")) {
                    return getEJBObjectId().hashCode();
                }
                else if (method.getName().equals("clone")) {
                    throw new CloneNotSupportedException(getEJBObjectId().toString());
                }
                else {
                    throw new UnsupportedOperationException("Unsupport Object Method: " + method);
                }
            }
            else {
                // 其它业务方法
                SecurityContext securityContext = new SecurityContext();
                SessionContext sessionContext = SessionContext.currentThreadSessionContext();
                if (sessionContext != null) {
                    // try get SecurityContext from session context
                    securityContext = sessionContext.getSecurityContext();
                }
                return getEJBContainer().invokeEJB(getEJBObjectId(), method, args, securityContext);
            }
        }

        EJBObjectId getEJBObjectId() {
            return ejbObjectId;
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

    // EJBContext Implementation
    public class EJBContextImpl extends AbstractEJBContext {

        public EJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

        public boolean getRollbackOnly() throws IllegalStateException {
            try {
                return getEJBContainer().getTransactionManager().getStatus() == Status.STATUS_MARKED_ROLLBACK;
            }
            catch (SystemException e) {
                throw new EJBException(e);
            }
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

        // Object method
        public String toString() {
            return "ejb_stub{name=" + getEJBName() + ",interface=" + Arrays.toString(getEJBInterfaces()) + "}";
        }

    }

}
