package org.jfox.webservice.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

import org.jfox.ejb3.naming.ContextAdapter;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InitialContextFactoryImpl implements InitialContextFactory, ObjectFactory {

    private static Context context = new WSContext();

    public Context getInitialContext(Hashtable<?,?> env) throws NamingException {
        if(context == null) {
            throw new NamingException("Naming Service not initialzed.");
        }
        return context;
    }

    public Object getObjectInstance(Object obj, Name name, Context ctx, Hashtable environment) throws Exception {
//        will perform after urlContextFactory failed and if set Context.OBJECT_FACTORY
        throw new UnsupportedOperationException("getObjectInstance");
    }

    static class WSContext extends ContextAdapter {
        public Object lookup(String name) throws NamingException {
            return super.lookup(name);
        }
    }

    public static void main(String[] args) {

    }
}