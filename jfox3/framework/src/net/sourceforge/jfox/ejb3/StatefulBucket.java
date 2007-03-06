package net.sourceforge.jfox.ejb3;

import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;

import net.sourceforge.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class StatefulBucket extends SessionBucket{

    /**
     * Stateful's contexts need a Map
     */
    private Map<EJBObjectId, Context> envContexts = new HashMap<EJBObjectId, Context>();

    private Map<EJBObjectId, SessionBucket.EJBContextImpl> contextMap = new HashMap<EJBObjectId, SessionBucket.EJBContextImpl>();

    protected StatefulBucket(EJBContainer container, Class<?> beanClass, Module module) {
        super(container, beanClass, module);
    }

    public static void main(String[] args) {

    }
}
