package net.sourceforge.jfox.framework.component;

import net.sourceforge.jfox.framework.BaseException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentNotFoundException extends BaseException {


    public ComponentNotFoundException() {
    }

    public ComponentNotFoundException(String message) {
        super(message);
    }

    public ComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
