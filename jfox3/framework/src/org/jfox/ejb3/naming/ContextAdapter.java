/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.naming;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Context Adapter, throw UnsupportedOperationException very method
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public abstract class ContextAdapter implements Context {

    public void bind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("bind");
    }

    public void rebind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("rebind");
    }

    public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException("unbind");
    }

    public Object lookup(String name) throws NamingException {
        throw new UnsupportedOperationException("lookup");
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException("addToEnvironment");
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException("removeFromEnvironment");
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new UnsupportedOperationException("getEnvironment");
    }

    public void bind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("bind");
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException("composeName");
    }

    public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException("composeName");
    }

    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("createSubcontext");
    }

    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException("createSubcontext");
    }

    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("destroySubcontext");
    }

    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException("destroySubcontext");
    }

    public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException("getNameInNamespace");
    }

    public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException("getNameParser");
    }

    public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException("getNameParser");
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new UnsupportedOperationException("list");
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new UnsupportedOperationException("list");
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException("listBindings");
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException("listBindings");
    }

    public Object lookup(Name name) throws NamingException {
        throw new UnsupportedOperationException("lookup");
    }

    public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException("lookupLink");
    }

    public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException("lookupLink");
    }

    public void rebind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException("rebind");
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        throw new UnsupportedOperationException("rename");
    }

    public void rename(String oldName, String newName) throws NamingException {
        throw new UnsupportedOperationException("rename");
    }

    public void unbind(Name name) throws NamingException {
        throw new UnsupportedOperationException("unbind");
    }

    public void close() throws NamingException {
        // do nothing
    }

}
