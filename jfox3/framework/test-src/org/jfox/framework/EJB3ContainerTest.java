package org.jfox.framework;

import java.io.File;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ejb.EJBObject;
import javax.ejb.Handle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.jfox.ejb3.EJBContainer;
import org.jfox.framework.example.AccountBO;
import org.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJB3ContainerTest {
    static Framework framework;
    @BeforeClass
    public static void setUp() throws Exception {
//        PlaceholderUtils.loadGlobalProperty(Constants.GLOBAL_PROPERTIES);
        framework = new Framework();
        Module petstoreModule = framework.loadModule(new File("MODULES/Petstore"));
        framework.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        framework.stop();
    }

    @Test
    public void testContainer() throws Exception {
        EJBContainer container = (EJBContainer)framework.getSystemModule().getComponent("EJB3Container");
    }

    @Test
    public void testEJB() throws Exception {
        Context ctx = new InitialContext();
        AccountBO accountMgr = (AccountBO)ctx.lookup("AccountBOImpl");
        System.out.println("!!!!! hashCode: " + accountMgr.hashCode());
        System.out.println("!!!!! Account name: " + accountMgr.getAccountName());
    }

    @Test
    public void testHandle() throws Exception {
        Context ctx = new InitialContext();
        AccountBO accountMgr = (AccountBO)ctx.lookup("AccountBOImpl");
        Handle handler = ((EJBObject)accountMgr).getHandle();
        EJBObject ejbObject = handler.getEJBObject();
        System.out.println("!!!!!!" + ((AccountBO)ejbObject).getAccountName());
        Assert.assertTrue(ejbObject.isIdentical((EJBObject)accountMgr));
        Assert.assertEquals(accountMgr,ejbObject);
    }

    public static void main(String[] args) {

    }
}
