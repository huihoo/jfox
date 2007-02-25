package net.sourceforge.jfox.entity;

import javax.persistence.PersistenceException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class NamedQueryNotFoundException extends PersistenceException {

    public NamedQueryNotFoundException() {
    }

    public NamedQueryNotFoundException(Throwable cause) {
        super(cause);
    }

    public NamedQueryNotFoundException(String message) {
        super(message);
    }

    public NamedQueryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}
