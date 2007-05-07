/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3.security;

import java.io.Serializable;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JAASPrincipal implements java.security.Principal, Serializable {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 5864848835776239991L;

    /**
     * Name of this principal.
     */
    private String name = null;

    /**
     * Constructor.
     *
     * @param name the name of this principal
     */
    public JAASPrincipal(final String name) {
        this.name = name;
    }

    /**
     * Compares this principal to the specified object. Returns true if the
     * object passed in matches the principal represented by the implementation
     * of this interface.
     *
     * @param that principal to compare with.
     * @return true if the principal passed in is the same as that encapsulated
     *         by this principal, and false otherwise.
     */
    @Override
    public boolean equals(final Object that) {
        if (!(that instanceof java.security.Principal)) {
            return false;
        }
        // else
        return name.equals(((java.security.Principal)that).getName());
    }

    /**
     * Returns a string representation of this principal.
     *
     * @return a string representation of this principal.
     */
    @Override
    public String toString() {
        return "Principal[" + name + "]";
    }

    /**
     * Returns a hashcode for this principal.
     *
     * @return a hashcode for this principal.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    public String getName() {
        return name;
    }

}
