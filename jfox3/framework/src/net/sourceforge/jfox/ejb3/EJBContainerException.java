package net.sourceforge.jfox.ejb3;

import net.sourceforge.jfox.framework.BaseRuntimeException;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
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
