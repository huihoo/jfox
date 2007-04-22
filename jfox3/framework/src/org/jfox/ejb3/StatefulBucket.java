package org.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;

import org.jfox.ejb3.dependent.FieldEJBDependence;
import org.jfox.ejb3.dependent.FieldResourceDependence;
import org.jfox.entity.dependent.FieldPersistenceContextDependence;
import org.jfox.framework.component.Module;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.ClassUtils;
import org.jfox.util.MethodUtils;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class StatefulBucket extends SessionBucket implements KeyedPoolableObjectFactory {

    /**
     * 保存的是StatefulEJBContext, key 为 EJBObjectId
     */
    private GenericKeyedObjectPool pool = new GenericKeyedObjectPool(this);

    protected List<Method> postActivateMethods = new ArrayList<Method>();
    protected List<Method> prePassivateMethods = new ArrayList<Method>();

    private volatile static long id = 0;

    protected StatefulBucket(EJBContainer container, Class<?> beanClass, Module module) {
        super(container, beanClass, module);
        // Stateless/Stateful 不同的Annotation
        introspectStateful();

        injectClassDependents();
    }

    protected void introspectStateful() {
        Stateful stateful = getBeanClass().getAnnotation(Stateful.class);
        String name = stateful.name();
        if (name.equals("")) {
            name = getBeanClass().getSimpleName();
        }
        setEJBName(name);

        String mappedName = stateful.mappedName();
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

        setDescription(stateful.description());

        // beanClass is in superClass array
        Class<?>[] superClasses = ClassUtils.getAllSuperclasses(getBeanClass());

        List<Long> postActivateMethodHashes = new ArrayList<Long>();
        List<Long> prePassivateMethodHashes = new ArrayList<Long>();
        for (Class<?> superClass : superClasses) {

            // PostConstruct
            for (Method postActivateMethod : introspectPostActivateMethod(superClass)) {
                long methodHash = MethodUtils.getMethodHash(postActivateMethod);
                if (!postActivateMethodHashes.contains(methodHash)) {
                    postActivateMethods.add(0, postActivateMethod);
                    postActivateMethodHashes.add(methodHash);
                }
            }

            // PreDestroy
            for (Method prePassivateMethod : introspectPrePassivateMethod(superClass)) {
                long methodHash = MethodUtils.getMethodHash(prePassivateMethod);
                if (!prePassivateMethodHashes.contains(methodHash)) {
                    prePassivateMethods.add(0, prePassivateMethod);
                    prePassivateMethodHashes.add(methodHash);
                }
            }
        }
    }

    protected List<Method> introspectPostActivateMethod(Class superClass) {
        List<Method> postConstructMethods = new ArrayList<Method>();
        // PostActivate
        Method[] _postConstructMethods = AnnotationUtils.getAnnotatedDeclaredMethods(superClass, PostActivate.class);
        for (Method postConstructMethod : _postConstructMethods) {
            postConstructMethod.setAccessible(true);
            postConstructMethods.add(0, postConstructMethod);
        }
        return postConstructMethods;
    }

    protected List<Method> introspectPrePassivateMethod(Class superClass) {
        List<Method> postConstructMethods = new ArrayList<Method>();
        // PrePassivate
        Method[] _postConstructMethods = AnnotationUtils.getAnnotatedDeclaredMethods(superClass, PrePassivate.class);
        for (Method postConstructMethod : _postConstructMethods) {
            postConstructMethod.setAccessible(true);
            postConstructMethods.add(0, postConstructMethod);
        }
        return postConstructMethods;
    }

    public boolean isStateless(){
        return false;
    }

    public boolean isWebService() {
        return false;
    }

    public EJBObjectId createEJBObjectId() {
        return new EJBObjectId(getEJBName(), "" + id++);
    }

    public AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        return new StatefulEJBContextImpl(ejbObjectId, instance);
    }

    public AbstractEJBContext getEJBContext(EJBObjectId ejbObjectId) {
        try {
            StatefulEJBContextImpl ejbContext = (StatefulEJBContextImpl)pool.borrowObject(ejbObjectId);
            return ejbContext;
        }
        catch (Exception e) {
            throw new EJBException("Create EJBContext failed, EJBObjectId=" + ejbObjectId, e);
        }
    }

    public void reuseEJBContext(AbstractEJBContext ejbContext) {
        try {
            pool.returnObject(ejbContext.getEJBObjectId(), ejbContext);
        }
        catch(Exception e) {
            throw new EJBException("Return EJBContext to pool failed!", e);
        }
    }

    //---- KeyedPoolableObjectFactory --------
    public void activateObject(Object key, Object obj) throws Exception {
    }

    public void passivateObject(Object key, Object obj) throws Exception {
    }

    public boolean validateObject(Object key, Object obj) {
        return true;
    }

    public void destroyObject(Object key, Object obj) throws Exception {
        //TODO: do @PrePassivate when pool destory EJBContext
        for (Method preDestroyMethod : getPreDestroyMethods()) {
            logger.debug("PreDestory method for ejb: " + getEJBName() + ", method: " + preDestroyMethod);
            preDestroyMethod.invoke(((AbstractEJBContext)obj).getEJBInstance());
        }
    }

    public Object makeObject(Object key) throws Exception {
        Object obj = getBeanClass().newInstance();
        AbstractEJBContext ejbContext = createEJBContext((EJBObjectId)key, obj);
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

    /**
     * destroy bucket, invoke when container unload ejb
     */
    public void stop() {
        logger.debug("Destroy EJB: " + getEJBName() + ", Module: " + getModule().getName());
        try {
            pool.clear();
            pool.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    class StatefulEJBContextImpl extends EJBContextImpl {

        public StatefulEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

    }

    public static void main(String[] args) {

    }
}
