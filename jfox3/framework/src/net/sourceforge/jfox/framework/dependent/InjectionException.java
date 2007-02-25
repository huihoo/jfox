package net.sourceforge.jfox.framework.dependent;

import net.sourceforge.jfox.framework.BaseException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InjectionException extends BaseException {

    public InjectionException() {
    }

    public InjectionException(Throwable cause) {
        super(cause);
    }

    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
