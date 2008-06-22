/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.mdb;

import org.jfox.ejb3.naming.InitialContextFactoryImpl;
import org.jfox.ejb3.naming.url.javaURLContextFactory;
import org.jfox.framework.Framework;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MDBClient {

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
        QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("defaultcf");
        QueueConnection connection = connectionFactory.createQueueConnection();
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("testQ");
        QueueSender sender = session.createSender(queue);
        Message message = session.createTextMessage("Hello, JMS! " + System.currentTimeMillis());
        System.out.println("Send Message: " + message);
        sender.send(message);
        Thread.sleep(2000);
        session.close();
        connection.close();

        // stop Framework
        Thread.sleep(2000);
        framework.stop();
    }
}
