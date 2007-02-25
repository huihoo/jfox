package net.sourceforge.jfox.ejb3;

import java.rmi.RemoteException;
import java.io.Serializable;
import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.naming.InitialContext;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJBHandleImpl implements Handle, Serializable {
    /**
     * ejb name
     */
    private String name;

    public EJBHandleImpl(String name) {
        this.name = name;
    }

    public EJBObject getEJBObject() throws RemoteException {
        try {
            InitialContext initContext = new InitialContext();
            return (EJBObject)initContext.lookup(name);
        }
        catch(Exception e) {
            throw new RemoteException("EJBHandleImpl.getEJBObject failed!",e);
        }
    }

    public static void main(String[] args) {

    }
}
