package net.sourceforge.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJBException;

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
    }

    public EJBObjectId createEJBObjectId() {
        return new EJBObjectId(getEJBName(), "" + id++);
    }

    public AbstractEJBContext newEJBContext(EJBObjectId ejbObjectId) throws EJBException {
        try {
            StatefulEJBContextImpl ejbContext = (StatefulEJBContextImpl)makeObject(ejbObjectId);
            return ejbContext;
        }
        catch (Exception e) {
            throw new EJBException("Create EJBContext failed, EJBObjectId=" + ejbObjectId, e);
        }
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

        //返回 EJBContext
        return createEJBContext((EJBObjectId)key, obj);
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
