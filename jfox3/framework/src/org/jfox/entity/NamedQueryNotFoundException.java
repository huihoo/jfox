/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import javax.persistence.PersistenceException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class NamedQueryNotFoundException extends PersistenceException {

    public NamedQueryNotFoundException() {
    }

    public NamedQueryNotFoundException(Throwable cause) {
        super(cause);
    }

    public NamedQueryNotFoundException(String message) {
        super(message);
    }

    public NamedQueryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
