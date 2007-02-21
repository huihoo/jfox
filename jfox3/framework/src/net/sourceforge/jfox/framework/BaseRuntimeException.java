package net.sourceforge.jfox.framework;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class BaseRuntimeException extends RuntimeException{


    public BaseRuntimeException() {
    }

    public BaseRuntimeException(String message) {
        super(message);
    }

    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
