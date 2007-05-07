/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import org.jfox.framework.BaseException;

/**
 * throw invocation exception when failed to build Invocation.
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InvocationException extends BaseException {

    public InvocationException() {

    }

    public InvocationException(Throwable cause) {
        super(cause);
    }

    public InvocationException(String message) {
        super(message);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
