/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms;

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

import org.jfox.jms.destination.JMSDestination;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSConsumer implements MessageConsumer, QueueReceiver, TopicSubscriber, MessageListener {
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

    // 通过 receive 方法同步接受到的消息
    private volatile Message currentReceivedMessage = null;

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
	}

	public synchronized Message receive() throws JMSException {
		checkClosed();
		return receive(0);
	}

	public synchronized Message receive(long timeout) throws JMSException {
		checkClosed();
        // connection is not started
        if(!session.getJMSConnection().isStarted()) {
            return null;
        }
        
        destination.registerMessageListener(this);
        try {
            //等待 onMessage 唤醒
            this.wait(timeout);
            Message tempMessage = currentReceivedMessage;
            currentReceivedMessage = null;
            return tempMessage;
        }
        catch(InterruptedException e) {
            return null;
        }
        finally{
            destination.unregisterMessageListener(this);
        }
    }

	public synchronized Message receiveNoWait() throws JMSException {
		checkClosed();
		return receive(1);
	}

	public void close() throws JMSException {
		if (closed) return;
		closed = true;
		session.removeConsumer(consumerId);
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
	 */
	public void setName(String name) {
		if (destination.isTopic()) {
			this.name = name;
		}
		durable = true;
	}

    //TODO: 默认 onMessage，在没有设置 MessageListener的情况，调用 receive 方法时，会用默认 MessageListener(this)，注册到 Queue 中
    public synchronized void onMessage(Message message) {
        // default message listener onMessage
        currentReceivedMessage = message;
        // notify wait in receive method
        this.notifyAll();
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
