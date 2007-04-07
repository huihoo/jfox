package jfox.test.ejb3.stateless;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jfox.framework.Framework;
import org.jfox.ejb3.naming.InitialContextFactoryImpl;
import org.jfox.ejb3.naming.url.javaURLContextFactory;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TestMain {

    public static void main(String[] args) throws Exception {
        // start Framework
        Framework framework = new Framework();
        framework.start();

        // initialize JNDI
        Hashtable<String, String> prop = new Hashtable<String, String>();
        prop.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryImpl.class.getName());
        prop.put(Context.OBJECT_FACTORIES, InitialContextFactoryImpl.class.getName());
        prop.put(Context.URL_PKG_PREFIXES, javaURLContextFactory.class.getPackage().getName());
        prop.put(Context.PROVIDER_URL, "java://localhost");
        Context context = new InitialContext(prop);

        // lookup calculator then invoke add method
        Calculator calculator = (Calculator)context.lookup("stateless.CalculatorBean/remote");
        int result = calculator.add(99,1);
        System.out.println("invoke calculator: 99+1=" + result);

        // stop Framework
        Thread.sleep(2000);
        framework.stop();
    }
}
