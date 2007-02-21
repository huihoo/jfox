package net.sourceforge.jfox.mvc;

import net.sourceforge.jfox.framework.BaseException;

/**
 * throw invocation exception when failed to build Invocation.
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
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
