/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.mdb;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Message;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.jfox.ejb3.naming.JNDIContextHelper;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Stateless
@Remote
public class MessageSenderBean implements MessageSender {

    @Resource
    QueueConnectionFactory queueConnectionFactory;

    public void sendQueueMessage(Message message) {
        try {
            // use injected jms connection factory
            QueueConnection qc = queueConnectionFactory.createQueueConnection();
            QueueSession qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            QueueSender sender = qs.createSender(qs.createQueue("testQ"));
            sender.send(qs.createTextMessage("Hello, Queue MDB!"));
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public void sendTopicMessage(Message message) {
        try {
            // lookup jms connection factory by jndi
            TopicConnectionFactory tcf = (TopicConnectionFactory)JNDIContextHelper.lookup("defaultcf");
            TopicConnection tc = tcf.createTopicConnection();
            TopicSession ts = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher tp = ts.createPublisher(ts.createTopic("testT"));
            tp.send(ts.createTextMessage("Hello, Topic MDB!"));
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

}
