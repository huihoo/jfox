package net.sourceforge.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJBException;
import javax.ejb.Stateful;

import net.sourceforge.jfox.ejb3.dependent.FieldEJBDependence;
import net.sourceforge.jfox.ejb3.dependent.FieldResourceDependence;
import net.sourceforge.jfox.entity.dependent.FieldPersistenceContextDependence;
import net.sourceforge.jfox.framework.component.Module;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class StatefulBucket extends SessionBucket implements KeyedPoolableObjectFactory {

    private GenericKeyedObjectPool pool = new GenericKeyedObjectPool(this);

    /**
     * Stateful's contexts need a Map，不需要，已经存在与 pool 中
     */
    private Map<EJBObjectId, StatefulEJBContextImpl> contextMap = new HashMap<EJBObjectId, StatefulEJBContextImpl>();

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
    }


    public EJBObjectId createEJBObjectId() {
        return new EJBObjectId(getEJBName(), "" + id++);
    }

    public AbstractEJBContext newEJBContext(EJBObjectId ejbObjectId) throws EJBException {
        try {
            StatefulEJBContextImpl ejbContext = (StatefulEJBContextImpl)pool.borrowObject(ejbObjectId);
            return ejbContext;
        }
        catch (Exception e) {
            throw new EJBException("Create EJBContext failed, EJBObjectId=" + ejbObjectId, e);
        }
    }

    public AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        return new StatefulEJBContextImpl(ejbObjectId, instance);
    }

    public void reuseEJBContext(AbstractEJBContext ejbContext) throws Exception {
        pool.returnObject(ejbContext.getEJBObjectId(), ejbContext);
    }

    //---- KeyedPoolableObjectFactory --------
    public void activateObject(Object key, Object obj) throws Exception {
    }

    public void destroyObject(Object key, Object obj) throws Exception {
    }

    public Object makeObject(Object key) throws Exception {
        Object obj = getBeanClass().newInstance();
        AbstractEJBContext ejbContext = createEJBContext((EJBObjectId)key, obj);
// post construct
        for (Method postConstructMethod : postConstructMethods) {
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

    public void passivateObject(Object key, Object obj) throws Exception {
    }

    public boolean validateObject(Object key, Object obj) {
        return true;
    }

    class StatefulEJBContextImpl extends EJBContextImpl {

        public StatefulEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

    }

    public static void main(String[] args) {

    }
}
