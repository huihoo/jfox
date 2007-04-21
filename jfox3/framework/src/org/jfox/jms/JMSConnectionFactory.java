/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnection;
import javax.jms.XATopicConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.QueueSender;
import javax.jms.QueueReceiver;
import javax.jms.Message;

import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.SingletonComponent;
import org.jfox.jms.connector.JMSContainer;
import org.jfox.jms.destination.JMSDestination;
import org.jfox.jms.destination.JMSQueue;
import org.jfox.jms.destination.JMSTopic;
import org.jfox.jms.message.JMSMessage;


/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

@Service(id = "defaultcf")
public class JMSConnectionFactory implements ConnectionFactory,
        QueueConnectionFactory,
        TopicConnectionFactory,
        XAConnectionFactory,
        XAQueueConnectionFactory,
        XATopicConnectionFactory,
        Serializable,
        Component,
        SingletonComponent,
        ActiveComponent {

    private transient JMSContainer container = null;

    private Map<String, JMSDestination> destinationMap = new HashMap<String, JMSDestination>();

    public Connection createConnection() throws JMSException {
        return createConnection(null, null);
    }

    public Connection createConnection(String userName, String password) throws JMSException {
        return createConnection(userName, password, false);
    }

    public QueueConnection createQueueConnection() throws JMSException {
        return (QueueConnection)createConnection();
    }

    public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        return (QueueConnection)createConnection(userName, password);
    }

    public TopicConnection createTopicConnection() throws JMSException {
        return (TopicConnection)createConnection();
    }

    public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        return (TopicConnection)createConnection(userName, password);
    }

    public XAConnection createXAConnection() throws JMSException {
        return createXAConnection(null, null);
    }

    public XAConnection createXAConnection(String userName, String password) throws JMSException {
        return createConnection(userName, password, true);
    }

    public XAQueueConnection createXAQueueConnection() throws JMSException {
        return (XAQueueConnection)createXAConnection();
    }

    public XAQueueConnection createXAQueueConnection(String userName, String password) throws JMSException {
        return (XAQueueConnection)createXAConnection(userName, password);
    }

    public XATopicConnection createXATopicConnection() throws JMSException {
        return (XATopicConnection)createXAConnection();
    }

    public XATopicConnection createXATopicConnection(String userName, String password) throws JMSException {
        return (XATopicConnection)createXAConnection(userName, password);
    }

    public void close(){
        for(JMSDestination destination : destinationMap.values()) {
            destination.stop();
        }
    }

    /**
     * TODO: create queue with given name
     *
     * TODO: 是否需要把该方法定义再 Interface 中，否则 framework 通过存根引用的实例无法调用???
     *
     * @param name queue name
     */
    JMSQueue createQueue(String name) throws JMSException {
        if (!destinationMap.containsKey(name)) {
            JMSQueue queue = new JMSQueue(name);
            destinationMap.put(name, queue);
            return queue;
        }
        else {
            JMSDestination destination = destinationMap.get(name);
            if(destination.isTopic()) {
                throw new JMSException("Destination " + name + " exists, but its type is Topic.");
            }
            return (JMSQueue)destination;
        }
    }

    JMSTopic createTopic(String name) throws JMSException {
        if (!destinationMap.containsKey(name)) {
            JMSTopic queue = new JMSTopic(name);
            destinationMap.put(name, queue);
            return queue;
        }
        else {
            JMSDestination destination = destinationMap.get(name);
            if(!destination.isTopic()) {
                throw new JMSException("Destination " + name + " exists, but its type is Queue.");
            }
            return (JMSTopic)destination;
        }
    }

    protected synchronized JMSConnection createConnection(String userName, String password, boolean isXA) {
        //TODO: support username & password
        return new JMSConnection(UUID.randomUUID().toString(), this, true);
    }

    public void sendMessage(JMSDestination destination, JMSMessage message) {

    }

    public static void main(String[] args) throws Exception {
        JMSConnectionFactory connectionFactory = new JMSConnectionFactory();
        QueueConnection connection = connectionFactory.createQueueConnection();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("defaultQ");
        QueueSender sender = session.createSender(queue);
        sender.send(session.createTextMessage("Hello, JMS!"));

        connection.start();
        QueueReceiver receiver = session.createReceiver(queue);
        Message message = receiver.receive();
        System.out.println("Received Message: " + message);

        Thread.sleep(2000);
        connectionFactory.close();
    }
}