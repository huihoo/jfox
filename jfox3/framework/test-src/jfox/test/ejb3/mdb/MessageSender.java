package jfox.test.ejb3.mdb;

import javax.jms.Message;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface MessageSender {

    void sendQueueMessage(Message message);

    void sendTopicMessage(Message message);
}
