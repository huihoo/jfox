package org.jfox.framework;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class BaseException extends Exception {


    public BaseException() {
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {
    }
}
