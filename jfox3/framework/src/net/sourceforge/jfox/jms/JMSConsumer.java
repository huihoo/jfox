/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms;

import java.util.UUID;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSConsumer implements MessageConsumer, QueueReceiver, TopicSubscriber {
	private JMSSession session = null;
	private JMSDestination destination = null;
	private String msgSelector = null;
	private boolean noLocal;

	/**
	 * P2P receiver is durable
	 * Durable Topic subscriber is durable
	 */
	private boolean durable = false;
	// only a durable TopicSubscriber has a name
	private String name = "";

	private MessageListener listener = null;

	private boolean closed = false;

	private String consumerId = UUID.randomUUID().toString();

	public JMSConsumer(JMSSession session, Destination destination, String msgSelector, boolean noLocal) {
		this.session = session;
		this.destination = (JMSDestination) destination;
		this.msgSelector = msgSelector;
		this.noLocal = noLocal;
		if (!this.destination.isTopic()) {
			durable = true;
		}
	}

	public String getMessageSelector() throws JMSException {
		checkClosed();
		throw new JMSException("not support now!");
	}

	public MessageListener getMessageListener() throws JMSException {
		checkClosed();
		return listener;
	}

	public void setMessageListener(MessageListener listener) throws JMSException {
		checkClosed();
		this.listener = listener;
		if (listener != null) {
			session.setConsumerAsync(this, true);
		} else {
			session.setConsumerAsync(this, false);
		}
	}

	public Message receive() throws JMSException {
		checkClosed();
		return receive(0);
	}

	public Message receive(long timeout) throws JMSException {
		checkClosed();
		return session.receiveMessage(this, timeout);
	}

	public Message receiveNoWait() throws JMSException {
		checkClosed();
		return receive(-1);
	}

	public void close() throws JMSException {
		if (closed) return;
		closed = true;
		session.closeConsumer(consumerId);
	}

	public Queue getQueue() throws JMSException {
		checkClosed();
		return (Queue) destination;
	}

	public Topic getTopic() throws JMSException {
		checkClosed();
		return (Topic) destination;
	}

	public boolean getNoLocal() throws JMSException {
		checkClosed();
		return noLocal;
	}

	public String getName() {
		return name;
	}

	/**
	 * only a durable TopicSubscriber has a name
	 *
	 * @param name
	 */
	public void setName(String name) {
		if (destination.isTopic()) {
			this.name = name;
		}
		durable = true;
	}

	String getConsumerId() {
		return consumerId;
	}

	Destination getDestination() {
		return destination;
	}

	private void checkClosed() throws javax.jms.IllegalStateException {
		if (closed) {
			throw new javax.jms.IllegalStateException("MessageConsumer closed");
		}
	}

	public static void main(String[] args) {

	}
}
