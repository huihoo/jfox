package org.jfox.webservice.hessian;

import org.jfox.ejb3.EJBContainer;
import org.jfox.ejb3.EJBObjectId;
import org.jfox.ejb3.remote.ContainerInvoker;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.SessionContext;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 5, 2008 10:13:43 PM
 */
@Service(priority = Integer.MIN_VALUE)
public class HessianContainerInvoker implements ContainerInvoker {

    @Inject
    EJBContainer ejbContainer;

    public Object invokeEJB(EJBObjectId ejbObjectId, Method method, Object[] params, SessionContext sessionContext) throws Exception {
        //TODO: HessianContainerInvoker.invokeEJB
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}