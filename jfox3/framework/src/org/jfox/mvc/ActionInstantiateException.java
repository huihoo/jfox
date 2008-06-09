package org.jfox.mvc;

import org.jfox.framework.BaseRuntimeException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jun 7, 2008 3:36:10 PM
 */
public class ActionInstantiateException extends BaseRuntimeException{

    public ActionInstantiateException(String message) {
        super(message);
    }

    public ActionInstantiateException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
