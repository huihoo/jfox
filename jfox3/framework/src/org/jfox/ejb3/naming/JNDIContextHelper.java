/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jfox.ejb3.naming.url.javaURLContextFactory;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JNDIContextHelper {

    private static InitialContext INITIAL_CONTEXT = null;

    public static synchronized  InitialContext getInitalContext() throws NamingException {
        if (INITIAL_CONTEXT == null) {
            Hashtable<String, String> prop = new Hashtable<String, String>();
            prop.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryImpl.class.getName());
            prop.put(Context.OBJECT_FACTORIES, InitialContextFactoryImpl.class.getName());
            prop.put(Context.URL_PKG_PREFIXES, javaURLContextFactory.class.getPackage().getName());
            prop.put(Context.PROVIDER_URL, "java://localhost");
            INITIAL_CONTEXT = new InitialContext(prop);
        }
        return INITIAL_CONTEXT;
    }

    public static Object lookup(String name) throws NamingException{
        return getInitalContext().lookup(name);
    }

    public static void main(String[] args) {

    }
}
