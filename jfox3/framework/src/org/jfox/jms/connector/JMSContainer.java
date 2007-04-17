/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.connector;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.jfox.jms.message.JMSMessage;

/**
 * JMS ����
 *
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public interface JMSContainer {// extends Container {

	public boolean auth(String userName, String password) throws JMSException;

	public Queue createQueue(java.lang.String queueName) throws JMSException;

	public Topic createTopic(String topicName) throws JMSException;

	public TemporaryQueue createTemporaryQueue() throws JMSException;

	public TemporaryTopic createTemporaryTopic() throws JMSException;

	public void registerConnection(String clientId, Object conn) throws JMSException;

	public void unregisterConnection(String clientId) throws JMSException;

	public boolean isConnectionRegistered(String clientId);

	public void registerSession(String connectionId, String sessionId) throws JMSException;

	public void sendMessage(JMSMessage msg) throws JMSException;

	public void sendMessageBatch(JMSMessage[] messages) throws JMSException;

	public JMSMessage receiveMessage(String clientId, String sessionId, String consumerId, long timeout) throws JMSException;

	public void registerConsumer(String connectionId,
	                             String sessionId,
	                             String consumerId,
	                             Destination destination) throws JMSException;

	public void setConsumerAsync(String clientId,
	                             String sessionId,
	                             String consumerId,
	                             boolean async) throws JMSException;

	public void startConnection(String clientId) throws JMSException;

	public void stopConnection(String clientId) throws JMSException;

	public void acknowledge(String clientId, String sessionId, String consumerId, String messageId) throws JMSException;

	public void closeSession(String clientId, String sessionId) throws JMSException;
}
