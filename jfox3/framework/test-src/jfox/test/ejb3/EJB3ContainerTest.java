/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3;

import java.io.File;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ejb.Handle;
import javax.ejb.EJBObject;

import org.jfox.framework.Framework;
import org.jfox.framework.component.Module;
import org.jfox.ejb3.EJBContainer;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import jfox.test.ejbcomponent.bo.AccountBO;

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
