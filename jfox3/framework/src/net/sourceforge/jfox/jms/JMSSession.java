/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.XAQueueSession;
import javax.jms.XASession;
import javax.jms.XATopicSession;
import javax.transaction.xa.XAResource;

import net.sourceforge.jfox.jms.message.BytesMessageImpl;
import net.sourceforge.jfox.jms.message.JMSMessage;
import net.sourceforge.jfox.jms.message.MapMessageImpl;
import net.sourceforge.jfox.jms.message.ObjectMessageImpl;
import net.sourceforge.jfox.jms.message.StreamMessageImpl;
import net.sourceforge.jfox.jms.message.TextMessageImpl;

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

	private Map<String, JMSConsumer> consumers = new HashMap<String, JMSConsumer>();

	private String sessionId = UUID.randomUUID().toString();

	private Map<String, JMSMessage> asyncMessages = new HashMap<String, JMSMessage>();

	public JMSSession(JMSConnection conn, boolean transacted, int acknowledgeMode, boolean isXA) {
		this.conn = conn;
		this.transacted = transacted;
		this.acknowledgeMode = acknowledgeMode;
		this.isXA = isXA;
		start();
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
		conn.closeSession(sessionId);
		synchronized (this) {
			notifyAll();
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
		start();
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
		//閿熸枻鎷?JMSContainer 閿熸枻鎷锋敞閿熸枻鎷穋onsumer
		getJMSConnection().getContainer().registerConsumer(getJMSConnection().getClientID(), getSessionId(), consumer.getConsumerId(), consumer.getDestination());
		consumers.put(consumer.getConsumerId(), consumer);
		return consumer;
	}

	public Queue createQueue(String queueName) throws JMSException {
		throw new JMSException("not support now!");
	}

	public Topic createTopic(String topicName) throws JMSException {
		throw new JMSException("not support now!");
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

	/**
	 * 閿熸枻鎷峰涓€閿熸枻鎷烽敓绔▼锝忔嫹閿熷眾姝ラ敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鎭?
	 */
	public void run() {
		while (!closed) {
			try {
				synchronized (this) {
					if (asyncMessages.isEmpty()) {
						wait();
					}
					if (closed) break;
				}
				for (Iterator it = asyncMessages.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, JMSMessage> entry = (Map.Entry<String, JMSMessage>) it.next();
					String consumerId = entry.getKey();
					JMSMessage message = entry.getValue();
					JMSConsumer consumer = consumers.get(consumerId);
					message.setSession(this);
					message.setConsumer(consumer);
					consumer.getMessageListener().onMessage(message);
					it.remove();
					if (this.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE) {
						acknowledge(consumer, message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	protected void start() {
		new Thread(this, "JMSSession-" + sessionId).start();
	}

	void sendMessage(Message message) throws JMSException {
		getJMSConnection().getContainer().sendMessage((JMSMessage) message);
	}

	/**
	 * @param timeout 0 forever; -1 noWait; >1 timeToWait
	 * @return
	 */
	JMSMessage receiveMessage(JMSConsumer consumer, long timeout) throws JMSException {
		if (!getJMSConnection().isStarted()) {
			throw new IllegalStateException("connection " + getJMSConnection().getClientID() + " not started, can't receive message.");
		}
		JMSMessage message = getJMSConnection().getContainer().receiveMessage(getJMSConnection().getClientID(),
		        getSessionId(),
		        consumer.getConsumerId(),
		        timeout);
		// acknowledge message
		if (getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE) {
			getJMSConnection().getContainer().acknowledge(getJMSConnection().getClientID(),
			        getSessionId(),
			        consumer.getConsumerId(),
			        message.getJMSMessageID());
		}
		return message;
	}

	protected synchronized void setConsumerAsync(JMSConsumer consumer, boolean async) throws JMSException {
		getJMSConnection().getContainer().setConsumerAsync(getJMSConnection().getClientID(),
		        getSessionId(),
		        consumer.getConsumerId(),
		        async);
	}

	protected void onMessage(String consumerId, JMSMessage message) {
		synchronized (this) {
			asyncMessages.put(consumerId, message);
			this.notifyAll();
		}
	}

	public void acknowledge(JMSConsumer consumer, JMSMessage message) throws JMSException {
		getJMSConnection().getContainer().acknowledge(getJMSConnection().getClientID(), sessionId, consumer.getConsumerId(), message.getJMSMessageID());
	}

	void closeConsumer(String consumerId) {
		consumers.remove(consumerId);
	}

	public static void main(String[] args) {

	}
}
