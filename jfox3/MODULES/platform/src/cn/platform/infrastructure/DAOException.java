package cn.iservicedesk.infrastructure;

import javax.ejb.EJBException;

/**
 * DAO Å×³öµÄÒì³£
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