package net.sourceforge.jfox.framework.component;

import net.sourceforge.jfox.framework.BaseException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ModuleResolvedFailedException extends BaseException {


    public ModuleResolvedFailedException(String message) {
        super(message);
    }

    public ModuleResolvedFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
