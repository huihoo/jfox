/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import org.jfox.framework.BaseException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentInstantiateException extends BaseException {


    public ComponentInstantiateException(String message) {
        super(message);
    }

    public ComponentInstantiateException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
