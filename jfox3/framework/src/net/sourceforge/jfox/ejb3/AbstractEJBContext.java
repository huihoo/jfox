package net.sourceforge.jfox.ejb3;

import java.rmi.RemoteException;
import java.security.Identity;
import java.security.Principal;
import java.util.Properties;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.ejb.RemoveException;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@SuppressWarnings({"deprecation"})
public abstract class AbstractEJBContext implements SessionContext, EJBObject, EJBLocalObject {

        private EJBObjectId ejbObjectId;
        private Object ejbInstance;

        public AbstractEJBContext(EJBObjectId ejbObjectId, Object ejbInstance) {
            this.ejbObjectId = ejbObjectId;
            this.ejbInstance = ejbInstance;
        }

        protected EJBObjectId getEJBObjectId() {
            return ejbObjectId;
        }

        protected Object getEJBInstance() {
            return ejbInstance;
        }

        public Principal getCallerPrincipal() {
            return null;
        }

        public EJBHome getEJBHome() {
            return null;
        }

        public EJBLocalHome getEJBLocalHome() {
            return null;
        }

        public UserTransaction getUserTransaction() throws IllegalStateException {
            //只支持 CMT, 不返回 UserTransaction
            return null;
        }

        public boolean isCallerInRole(final String roleName) {
            return false;
        }

        @Deprecated
        public Identity getCallerIdentity() {
            return null;
        }

        @Deprecated
        public Properties getEnvironment() {
            return null;
        }

        @Deprecated
        public boolean isCallerInRole(final Identity role) {
            return false;
        }

        // SessionContext
        public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
            throw new IllegalStateException("Can not invoke getBusinessObject!");
        }

        public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
            return this;
        }

        public EJBObject getEJBObject() throws IllegalStateException {
            return this;
        }

        public Class getInvokedBusinessInterface() throws IllegalStateException {
            return EJBInvocation.current().getInterfaceMethod().getDeclaringClass();
        }

        public MessageContext getMessageContext() throws IllegalStateException {
            return null;
        }

        public TimerService getTimerService() throws IllegalStateException {
            throw new IllegalStateException("Can not call getTimerService!");
        }

        // EJBObject & EJBLocalObject

        public Handle getHandle() throws RemoteException {
            return new EJBHandleImpl(getEJBObjectId());
        }

        public Object getPrimaryKey() {
            return getEJBObjectId();
        }

        public boolean isIdentical(EJBObject obj) throws RemoteException {
            return obj.getPrimaryKey().equals(getPrimaryKey());
        }

        public void remove() throws RemoveException {
            throw new RemoveException("Can not invoke remove()!");
        }

        public boolean isIdentical(EJBLocalObject obj) throws EJBException {
            return obj.getPrimaryKey().equals(getPrimaryKey());
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof EJBObjectId)) {
                return false;
            }
            else {
                try {
                    return isIdentical((EJBObject)obj);
                }
                catch (Exception e) {
                    return false;
                }
            }
        }

        public int hashCode() {
            return getEJBObjectId().hashCode();
        }

        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException(getEJBObjectId().toString());
        }

}
