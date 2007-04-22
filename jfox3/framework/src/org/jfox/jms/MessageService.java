package org.jfox.jms;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnectionFactory;

import org.jfox.jms.destination.JMSQueue;
import org.jfox.jms.destination.JMSTopic;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface MessageService extends ConnectionFactory,
        QueueConnectionFactory,
        TopicConnectionFactory,
        XAConnectionFactory,
        XAQueueConnectionFactory,
        XATopicConnectionFactory,
        Serializable {

    JMSQueue createQueue(String name) throws JMSException;

    JMSTopic createTopic(String name) throws JMSException;
}
