package net.sourceforge.jfox.framework.component;

import net.sourceforge.jfox.framework.BaseRuntimeException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentInvocationException extends BaseRuntimeException {

    public ComponentInvocationException() {
    }

    public ComponentInvocationException(String message) {
        super(message);
    }

    public ComponentInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
