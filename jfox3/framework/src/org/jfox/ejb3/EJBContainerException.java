/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

import org.jfox.framework.BaseRuntimeException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJBContainerException extends BaseRuntimeException {

    public EJBContainerException() {
        super();
    }

    public EJBContainerException(String message) {
        super(message);
    }

    public EJBContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
