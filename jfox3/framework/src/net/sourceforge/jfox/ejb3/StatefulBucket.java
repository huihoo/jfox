package net.sourceforge.jfox.ejb3;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.ejb.EJBObject;

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

    public Context getENContext(EJBObjectId ejbObjectId) {
        return null;
    }

    public EJBObject createProxyStub() {
        return null;
    }

    public AbstractEJBContext newEJBContext(EJBObjectId ejbObjectId) throws Exception {
        return null;
    }

    public void reuseEJBContext(AbstractEJBContext ejbContext) throws Exception {
    }//---- KeyedPoolableObjectFactory --------
    public void activateObject(Object key, Object obj) throws Exception {
    }

    public void destroyObject(Object key, Object obj) throws Exception {
    }

    public Object makeObject(Object key) throws Exception {
        return null;
    }

    public void passivateObject(Object key, Object obj) throws Exception {
    }

    public boolean validateObject(Object key, Object obj) {
        return false;
    }

    class StatefulEJBContextImpl extends EJBContextImpl {

        public StatefulEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

    }

    public static void main(String[] args) {

    }
}
