/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms;

import java.util.UUID;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

import org.jfox.jms.destination.JMSDestination;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSProducer implements MessageProducer, QueueSender, TopicPublisher {
	/**
	 * The default delivery mode
	 */
	private int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
	/**
	 * The default priorty
	 */
	private int priority = Message.DEFAULT_PRIORITY;

	private long timeToLive = Message.DEFAULT_TIME_TO_LIVE;

	/**
	 * The disable message id flag
	 */
	private boolean disableMessageID = false;
	/**
	 * The disable message timestamp flag
	 */
	private boolean disableTimestamp = false;

	private Destination destination;
	private JMSSession session;

	private boolean closed = false;

	public JMSProducer(JMSSession session, Destination destination) {
		this.session = session;
		this.destination = destination;
	}

	public void setDisableMessageID(boolean disableMessageID) throws JMSException {
		checkClosed();
		this.disableMessageID = disableMessageID;
	}

	public boolean getDisableMessageID() throws JMSException {
		return disableMessageID;
	}

	public void setDisableMessageTimestamp(boolean disableMessageTimestamp) throws JMSException {
		checkClosed();
		this.disableTimestamp = disableMessageTimestamp;
	}

	public boolean getDisableMessageTimestamp() throws JMSException {
		return disableTimestamp;
	}

	public void setDeliveryMode(int deliveryMode) throws JMSException {
		checkClosed();
		validateDeliveryMode(deliveryMode);
		this.deliveryMode = deliveryMode;
	}

	public int getDeliveryMode() throws JMSException {
		return deliveryMode;
	}

	public void setPriority(int priority) throws JMSException {
		checkClosed();
		validatePriority(priority);
		this.priority = priority;
	}

	public int getPriority() throws JMSException {
		return priority;
	}

	public void setTimeToLive(long timeToLive) throws JMSException {
		checkClosed();
		this.timeToLive = timeToLive;
	}

	public long getTimeToLive() throws JMSException {
		return timeToLive;
	}

	public Destination getDestination() throws JMSException {
		return destination;
	}

	public void close() throws JMSException {
		this.closed = true;
	}

	public void send(Message message) throws JMSException {
		send(destination, message, deliveryMode, priority, timeToLive);
	}

	public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
		send(destination, message, deliveryMode, priority, timeToLive);
	}

	public void send(Destination destination, Message message) throws JMSException {
		send(destination, message, deliveryMode, priority, timeToLive);
	}

	public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
		checkClosed();
		if (destination == null)
			throw new JMSException("Null destination");
		if (message == null)
			throw new JMSException("Null message");
		if (!disableMessageID) {
			message.setJMSMessageID("ID:" + UUID.randomUUID().toString());
		}

		if (!disableTimestamp) {
			message.setJMSTimestamp(System.currentTimeMillis());
		}

		message.setJMSDestination(destination);
		message.setJMSDeliveryMode(deliveryMode);
		message.setJMSPriority(priority);

		if (!disableTimestamp && timeToLive != 0) {
			message.setJMSExpiration(message.getJMSTimestamp() + timeToLive);
		}

        //TODO: 要考虑 Session 支持事务的情况

        ((JMSDestination)destination).putMessage(message);
    }

	public Queue getQueue() throws JMSException {
		return (Queue) destination;
	}

	public void send(Queue queue, Message message) throws JMSException {
		send(queue, message, deliveryMode, priority, timeToLive);
	}

	public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
		send((Destination) queue, message, deliveryMode, priority, timeToLive);
	}

	public Topic getTopic() throws JMSException {
		return (Topic) destination;
	}

	public void publish(Message message) throws JMSException {
		send(message);
	}

	public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
		send(destination, message, deliveryMode, priority, timeToLive);
	}

	public void publish(Topic topic, Message message) throws JMSException {
		send(topic, message);
	}

	public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
		send(topic, message, deliveryMode, priority, timeToLive);
	}

	/**
	 * validate the deliveryMode
	 *
	 * @param deliveryMode
	 * @throws JMSException
	 */
	static void validateDeliveryMode(int deliveryMode) throws JMSException {
		if (deliveryMode != DeliveryMode.NON_PERSISTENT &&
		        deliveryMode != DeliveryMode.PERSISTENT)
			throw new JMSException("Invalid delivery mode " + deliveryMode);
	}

	/**
	 * Validate the priority
	 */
	static void validatePriority(int priority) throws JMSException {
		if (priority < 0 || priority > 9) {
			throw new JMSException("Invalid priority " + priority);
		}
	}

	private void checkClosed() throws javax.jms.IllegalStateException {
		if (closed) {
			throw new javax.jms.IllegalStateException("MessageProducer closed");
		}
	}

	public static void main(String[] args) {

	}
}
