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
import javax.jms.*;
import javax.transaction.xa.XAResource;

import org.jfox.jms.message.BytesMessageImpl;
import org.jfox.jms.message.JMSMessage;
import org.jfox.jms.message.MapMessageImpl;
import org.jfox.jms.message.ObjectMessageImpl;
import org.jfox.jms.message.StreamMessageImpl;
import org.jfox.jms.message.TextMessageImpl;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSSession implements Session,
        QueueSession,
        TopicSession,
        XASession,
        XAQueueSession,
        XATopicSession {

	private JMSConnection conn;

	private boolean transacted;

	private int acknowledgeMode;

	private boolean isXA;

	private boolean closed = false;

	private MessageListener listener;

	private Map<String, JMSConsumer> consumerMap = new HashMap<String, JMSConsumer>();
    private Map<String, JMSProducer> producerMap = new HashMap<String, JMSProducer>();

    private String sessionId = UUID.randomUUID().toString();

	public JMSSession(JMSConnection conn, boolean transacted, int acknowledgeMode, boolean isXA) {
		this.conn = conn;
		this.transacted = transacted;
		this.acknowledgeMode = acknowledgeMode;
		this.isXA = isXA;
	}

	public BytesMessage createBytesMessage() throws JMSException {
		checkClosed();
		return new BytesMessageImpl();
	}

	public MapMessage createMapMessage() throws JMSException {
		checkClosed();
		return new MapMessageImpl();
	}

	public Message createMessage() throws JMSException {
		checkClosed();
		return new JMSMessage();
	}

	public ObjectMessage createObjectMessage() throws JMSException {
		checkClosed();
		return new ObjectMessageImpl();
	}

	public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
		checkClosed();
		ObjectMessageImpl om = new ObjectMessageImpl();
		om.setObject(object);
		return om;
	}

	public StreamMessage createStreamMessage() throws JMSException {
		checkClosed();
		return new StreamMessageImpl();
	}

	public TextMessage createTextMessage() throws JMSException {
		checkClosed();
		return new TextMessageImpl();
	}

	public TextMessage createTextMessage(String text) throws JMSException {
		checkClosed();
		TextMessageImpl tm = new TextMessageImpl();
		tm.setText(text);
		return tm;
	}

	public boolean getTransacted() throws JMSException {
		return transacted;
	}

	public int getAcknowledgeMode() throws JMSException {
		return acknowledgeMode;
	}

	public synchronized void commit() throws JMSException {
		checkClosed();
		throw new JMSException("not support now!");
	}

	public synchronized void rollback() throws JMSException {
		checkClosed();
		throw new JMSException("not support now!");
	}

	public synchronized void close() throws JMSException {
		if (closed) return;
		this.closed = true;
		conn.removeSession(sessionId);
        for(JMSConsumer consumer : consumerMap.values()){
            consumer.close();
        }
    }

	public synchronized void recover() throws JMSException {
		throw new JMSException("not support now!");
	}

	public MessageListener getMessageListener() throws JMSException {
		return listener;
	}

	public void setMessageListener(MessageListener listener) throws JMSException {
		checkClosed();
		this.listener = listener;
	}

	public MessageProducer createProducer(Destination destination) throws JMSException {
		if (destination == null) {
			throw new InvalidDestinationException("destination is null");
		}
		JMSProducer producer = new JMSProducer(this, destination);
		return producer;
	}

	public MessageConsumer createConsumer(Destination destination) throws JMSException {
		return createConsumer(destination, null);
	}

	public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
		return createConsumer(destination, messageSelector, false);
	}

	public synchronized MessageConsumer createConsumer(Destination destination,
	                                                   String messageSelector,
	                                                   boolean NoLocal) throws JMSException {
		if (destination == null) {
			throw new InvalidDestinationException("destination is null");
		}
		JMSConsumer consumer = new JMSConsumer(this, destination, messageSelector, NoLocal);
		consumerMap.put(consumer.getConsumerId(), consumer);
		return consumer;
	}

	public Queue createQueue(String queueName) throws JMSException {
        Queue queue = getJMSConnection().getConnectionFactory().createQueue(queueName);
        return queue;
	}

	public Topic createTopic(String topicName) throws JMSException {
		Topic topic = getJMSConnection().getConnectionFactory().createTopic(topicName);
        return topic;
    }

	public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
		throw new JMSException("not support now!");
	}

	public TopicSubscriber createDurableSubscriber(Topic topic,
	                                               String name,
	                                               String messageSelector,
	                                               boolean noLocal) throws JMSException {
		throw new JMSException("not support now!");
	}

	public QueueBrowser createBrowser(Queue queue) throws JMSException {
		throw new JMSException("not support now!");
	}

	public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
		throw new JMSException("not support now!");
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
		throw new JMSException("not support now!");
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException {
		throw new JMSException("not support now!");
	}

	public void unsubscribe(String name) throws JMSException {
		throw new JMSException("not support now!");
	}

	public QueueReceiver createReceiver(Queue queue) throws JMSException {
		return createReceiver(queue, null);
	}

	public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException {
		return (QueueReceiver) createConsumer(queue, messageSelector);
	}

	public QueueSender createSender(Queue queue) throws JMSException {
		throw new JMSException("not support now!");
	}

	public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
		return createSubscriber(topic, null, false);
	}

	public TopicSubscriber createSubscriber(Topic topic,
	                                        String messageSelector,
	                                        boolean noLocal) throws JMSException {
		return (TopicSubscriber) createConsumer(topic, messageSelector, noLocal);
	}


	public TopicPublisher createPublisher(Topic topic) throws JMSException {
		return (TopicPublisher) createProducer(topic);
	}

	public Session getSession() throws JMSException {
		return this;
	}

	public XAResource getXAResource() {
		if (isXA == false) {
			throw new java.lang.IllegalStateException("current session " + this + " is not an XASession");
		}
		//TODO: getXAResource
		return null;
	}

	public QueueSession getQueueSession() throws JMSException {
		return (QueueSession) getSession();
	}

	public TopicSession getTopicSession() throws JMSException {
		return (TopicSession) getSession();
	}

	public void run() {
        // do nothing
	}

	private void checkClosed() throws javax.jms.IllegalStateException {
		if (closed) {
			throw new javax.jms.IllegalStateException("connection closed");
		}
	}

	/**
	 * get session id
	 *
	 * @return String
	 */
	protected String getSessionId() {
		return sessionId;
	}

	JMSConnection getJMSConnection() {
		return conn;
	}
    
	void start() {
		
	}

	void removeConsumer(String consumerId) {
		consumerMap.remove(consumerId);
	}

    void removeProducer(String producerId){
        producerMap.remove(producerId);
    }

    public static void main(String[] args) {

	}
}
