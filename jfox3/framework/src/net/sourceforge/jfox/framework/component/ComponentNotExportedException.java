package net.sourceforge.jfox.framework.component;

import net.sourceforge.jfox.framework.BaseException;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class ComponentNotExportedException extends BaseException {


    public ComponentNotExportedException() {
    }

    public ComponentNotExportedException(String message) {
        super(message);
    }

    public ComponentNotExportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
