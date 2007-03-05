package net.sourceforge.jfox.ejb3;

import java.util.Map;
import java.util.HashMap;
import javax.naming.Context;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class StatefulBucket implements EJBBucket{

    /**
     * Stateful's contexts need a Map
     */
    private Map<EJBObjectId, Context> envContexts = new HashMap<EJBObjectId, Context>();

    public static void main(String[] args) {

    }
}
