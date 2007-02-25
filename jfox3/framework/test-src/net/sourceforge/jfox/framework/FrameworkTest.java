package net.sourceforge.jfox.framework;

import java.io.File;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import net.sourceforge.jfox.framework.component.Component;
import net.sourceforge.jfox.framework.component.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FrameworkTest {

    private static Framework framework;

    private final static String module1 = "Test";
    private final static String module2 = "Test2";

    @BeforeClass
    public static void setUp() throws Exception {
//        PlaceholderUtils.loadGlobalProperty(Constants.GLOBAL_PROPERTIES);
        framework = new Framework();
        Module test1Module = framework.loadModule(new File("MODULES/Test"));
        Module test2Module = framework.loadModule(new File("MODULES/Test2"));
        Module petstoreModule = framework.loadModule(new File("MODULES/petstore"));
//        test1Module.start();
//        test2Module.start();
//        petstoreModule.start();
        framework.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        framework = null;
    }

    @Before
    public void beforeMethod() throws Exception {

    }

    @After
    public void afterMethod() throws Exception {
        
    }


    @Test
    public void loadModule() throws Exception {

    }

    @Test
    public void invokeComponent() throws Exception {
        Component component = framework.getModule(module1).getComponent(new ComponentId("User1"));
        System.out.println("Hello, " + component.getClass().getMethod("getName").invoke(component) + " !");

        Component component2 = framework.getModule(module2).getComponent(new ComponentId("User2"));
        System.out.println("Hello, " + component2.getClass().getMethod("getName").invoke(component2) + " : " + component2.getClass().getMethod("getPassword").invoke(component2) + " !");

        Component component3 = framework.getModule(module1).getComponent(new ComponentId("UserManager"));
        System.out.println("Hello, " + component3.getClass().getMethod("listUsers").invoke(component3) + " !");

    }

    @Test
    public void unloadModule() throws Exception {
        framework.unloadModule(module2);
        Assert.assertNull(framework.getModule(module2));
    }

    @Test
    public void reloadModule() throws Exception {
        Thread.sleep(10000);
        Module test2Module = framework.loadModule(new File("MODULES/Test2"));
        test2Module.start();
        Component component2 = framework.getModule(module2).getComponent(new ComponentId("User2"));
        System.out.println("Hello, " + component2.getClass().getMethod("getName").invoke(component2) + " : " + component2.getClass().getMethod("getPassword").invoke(component2) + " !");

        Component component3 = framework.getModule(module1).getComponent("UserManager");
        System.out.println("Hello, " + component3.getClass().getMethod("listUsers").invoke(component3) + " !");
        
    }

}
