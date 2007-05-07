/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

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
    private EJBObjectId ejbObjectId;

    public EJBHandleImpl(EJBObjectId ejbObjectId) {
        this.ejbObjectId = ejbObjectId;
    }

    public EJBObject getEJBObject() throws RemoteException {
        try {
            InitialContext initContext = new InitialContext();
            return (EJBObject)initContext.lookup(ejbObjectId.getEJBName());
        }
        catch(Exception e) {
            throw new RemoteException("EJBHandleImpl.getEJBObject failed!",e);
        }
    }

    public static void main(String[] args) {

    }
}
