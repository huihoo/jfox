package jfox.platform.infrastructure;

import javax.ejb.EJBException;

/**
 * DAO 抛出的异常
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class DAOException extends EJBException {

    public DAOException(String msg) {
        super(msg);
    }

    public DAOException(Exception e) {
        super(e);
    }

    public DAOException(String msg, Exception e) {
        super(msg, e);
    }

}