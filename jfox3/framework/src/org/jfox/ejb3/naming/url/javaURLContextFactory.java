package org.jfox.ejb3.naming.url;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class javaURLContextFactory implements ObjectFactory {

    /**
     * @param url  String with a "java:" prefix or null.
     * @param name Name of context, relative to ctx, or null.
     * @param ctx  Context relative to which 'name' is named.
     * @param env  Environment to use when creating the context *
     * @return an instance of javaURLContext for a java URL. If url is null, the
     *         result is a context for resolving java URLs. If url is a URL, the
     *         result is a context named by the URL.
     * @throws Exception if this object factory encountered an exception while
     *                   attempting to create an object, and no other object factories are
     *                   to be tried.
     */
    public Object getObjectInstance(Object url, Name name, Context ctx, Hashtable env) throws Exception {

        if (url == null) {
            // All naming operations with "java:..." comes here
            // Users are encouraged to used intermediate contexts:
            // ctx = ic.lookup("java:comp/env") called only once (perfs)
            return new InitialContext(env);
        }
        if (url instanceof String) {
            return null;
        }
        else if (url instanceof String[]) {
            // Don't know what to do here
            return null;
        }
        else {
            // invalid argument
            throw (new IllegalArgumentException("javaURLContextFactory"));
        }
    }
}
