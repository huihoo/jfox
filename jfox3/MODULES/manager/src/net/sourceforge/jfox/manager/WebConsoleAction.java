package net.sourceforge.jfox.manager;

import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 *
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class WebConsoleAction {

    //DataSource, NamedNativeQuery, PersistenceUnit
    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetJPAAction(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetModulesAction(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetJTAAction(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetJNDIAction(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetEJBContainerAction(InvocationContext invocationContext) throws Exception{

    }

    public static void main(String[] args) {

    }
}
