package jfox.test.ejb3;

import java.util.Arrays;
import java.util.Map;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.naming.Context;

import jfox.test.ejb3.entity.Order;
import jfox.test.ejb3.lob.Lobber;
import org.jfox.ejb3.naming.JNDIContextHelper;
import org.jfox.ejb3.security.JAASLoginServiceImpl;
import org.jfox.ejb3.security.SampleCallbackHandler;
import org.jfox.entity.dao.MapperEntity;
import org.jfox.framework.Framework;
import org.jfox.mvc.SessionContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TestClient {

    private static Framework framework;

    public static final String MODULE_NAME = "ejb3_exmple";

    @BeforeClass
    public static void setUp() throws Exception {
//        PlaceholderUtils.loadGlobalProperty(Constants.GLOBAL_PROPERTIES);
        framework = new Framework();
//        framework.loadModule(new File("MODULES/ejbtest"));
        framework.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.out.println("Waiting for tearDown...");
        Thread.sleep(5000);
        framework.stop();
        framework = null;
    }

    @Before
    public void beforeMethod() throws Exception {

    }

    @After
    public void afterMethod() throws Exception {

    }

    @Test
    public void invokeStateless() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.stateless.Calculator calculator = (jfox.test.ejb3.stateless.Calculator)context.lookup("stateless.CalculatorBean/remote");
        Assert.assertEquals(calculator.add(100, 1), 101);
        Assert.assertEquals(calculator.subtract(100, 1), 99);
    }

    @Test
    public void invokeEnv() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.env.Calculator calculator = (jfox.test.ejb3.env.Calculator)context.lookup("env.CalculatorBean/remote");
        calculator.remember(100);
        Assert.assertEquals(calculator.takeout(), 100);

        calculator.clear();
        try {
            Assert.assertEquals(calculator.takeout(), 100);
            Assert.fail("memory not clear!");
        }
        catch (EJBException e) {
            // expect ejbexception in calculator.takeout() 
        }
    }

    @Test
    public void invokeInjection() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.injection.ShoppingCart cart = (jfox.test.ejb3.injection.ShoppingCart)context.lookup("injection.ShoppingCartBean/remote");
        cart.buy("apple", 3);
        cart.buy("banana", 4);
        cart.buy("apple", 7);
        Map<String, Integer> contents = cart.getCartContents();
        Assert.assertEquals(contents.get("apple"), 10);
    }

    @Test
    public void invokeCallback() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.callback.Calculator calculator = (jfox.test.ejb3.callback.Calculator)context.lookup("callback.CalculatorBean/remote");
        Assert.assertEquals(calculator.add(100, 1), 101);
        Assert.assertEquals(calculator.subtract(100, 1), 99);

        ((EJBObject)calculator).remove();
    }

    @Test
    public void invokeInterceptor() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.interceptor.Calculator calculator = (jfox.test.ejb3.interceptor.Calculator)context.lookup("interceptor.CalculatorBean/remote");
        Assert.assertEquals(calculator.add(100, 1), 101);
        Assert.assertEquals(calculator.subtract(100, 1), 99);

        ((EJBObject)calculator).remove();
    }

    @Test
    public void invokeTimer() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.timer.ExampleTimer calculator = (jfox.test.ejb3.timer.ExampleTimer)context.lookup("timer.ExampleTimerBean/remote");
        calculator.scheduleTimer(500);
    }

    @Test
    public void invokeEntity() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.entity.OrderDAO orderDAO = (jfox.test.ejb3.entity.OrderDAO)context.lookup("entity.OrderDAO/remote");
        for (Order order : orderDAO.getOrders()) {
            System.out.println(order);
        }
    }

    @Test
    public void invokeLob() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.lob.LobberDAO lobberDAO = (jfox.test.ejb3.lob.LobberDAO)context.lookup("lob.LobberDAO/remote");
        Lobber lobber = MapperEntity.newEntityObject(Lobber.class);
        lobber.setId(1);
        String clobby = "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work" +
                "This is a very long string that will be stored in a java.sql.Clob hopefully.  We'll see how this works and if it will work";
        lobber.setClobby(clobby);
        lobber.setBlobby(clobby.getBytes());
        lobberDAO.insertLobber(lobber);

        lobber.setClobby("Hello,World!");
        lobberDAO.updateLobber(lobber);

        Lobber getLobber = lobberDAO.getLobber(1);
        Assert.assertTrue(Arrays.equals(getLobber.getBlobby(), lobber.getBlobby()));
    }

    @Test
    public void invokeStateful() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.stateful.ShoppingCart calculator = (jfox.test.ejb3.stateful.ShoppingCart)context.lookup("stateful.ShoppingCartBean/remote");
        calculator.buy("apple", 1);
        calculator.buy("banana", 2);
        Assert.assertEquals(calculator.getCartContents().size(), 2);

    }

    @Test
    public void invokeSynchronization() throws Exception {
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.synchronization.ShoppingCart calculator = (jfox.test.ejb3.synchronization.ShoppingCart)context.lookup("sychronization.ShoppingCartBean/remote");
        calculator.buy("apple", 1);
        calculator.buy("banana", 2);
        Assert.assertEquals(calculator.getCartContents().size(), 2);

    }

    @Test
    public void invokeSecurity() throws Exception {
        JAASLoginServiceImpl loginService = new JAASLoginServiceImpl();
        loginService.postPropertiesSet();
        loginService.login(SessionContext.currentThreadSessionContext(), new SampleCallbackHandler(), "role1", "1234");
        Context context = JNDIContextHelper.getInitalContext();
        jfox.test.ejb3.security.Calculator calculator = (jfox.test.ejb3.security.Calculator)context.lookup("security.CalculatorBean/remote");
        Assert.assertEquals(calculator.add(100, 1), 101);
        Assert.assertEquals(calculator.subtract(100, 1), 99);

    }

}
