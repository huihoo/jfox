/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.apache.log4j.Logger;
import net.sourceforge.jfox.jms.JMSConnectionRemote;
import net.sourceforge.jfox.jms.message.JMSMessage;
import net.sourceforge.jfox.framework.component.ActiveComponent;


/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSContainerImpl implements JMSContainer, ActiveComponent {

    Logger logger = Logger.getLogger(JMSContainerImpl.class);

    /**
	 * �����������4��������Ϣ�� Queue
	 */
	private static Random rand = new Random();
	/**
	 * all destinations
	 */
	private JMSDestinations destinations = JMSDestinations.getInstance();

	/**
	 * �����µ�4����Ϣ���ȴ��������ȴ�ַ�
	 */
	private static List<JMSMessage> tempMessages = new ArrayList<JMSMessage>();

	/**
	 * Client connections ClientID=>JMSConnectionRemote stub
	 * for async call back
	 */
	private Map<String, ConnectionMeta> connections = new HashMap<String, ConnectionMeta>();

	private String user = "";
	private String password = "";

	public JMSContainerImpl() {

	}

    public Class getHandlerClass() {
        return JMSHandler.class;
    }

	public boolean auth(String userName, String password) throws JMSException {
		if (user.equals("") && this.password.equals("")) {
			return true;
		} else {
			if (user.equals(userName) && this.password.equals(password)) {
				return true;
			} else {
				throw new JMSSecurityException("auth error, used user=" + user + ", password=" + password);
			}
		}
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Queue createQueue(java.lang.String queueName) throws JMSException {
		return null;
	}

	public Topic createTopic(String topicName) throws JMSException {
		return null;
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
		return null;
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException {
		return null;
	}

	public synchronized void registerConnection(String clientId, Object conn) throws JMSException {
//		JMSConnectionRemote jmsconn = (JMSConnectionRemote) Marshaller.unmarshall(conn);
        JMSConnectionRemote jmsconn = (JMSConnectionRemote)conn;
        logger.debug("register jms client connection: " + clientId + " => " + jmsconn);
		if (connections.containsKey(clientId)) {
			throw new JMSException("connection clientId " + clientId + " already registered.");
		}
		ConnectionMeta meta = new ConnectionMeta(clientId, jmsconn);
		connections.put(clientId, meta);
	}

	public synchronized void unregisterConnection(String clientId) throws JMSException {
		logger.debug("unregisterConnection clientId=" + clientId);
		ConnectionMeta meta = connections.remove(clientId);
		if (meta != null) {
			meta.close();
		}
	}

	public synchronized boolean isConnectionRegistered(String clientId) {
		return connections.containsKey(clientId);
	}

	public void registerSession(String connectionId, String sessionId) throws JMSException {
		logger.debug("register jms session, sessionId = " + sessionId + ", clientId = " + connectionId);
		if (!connections.containsKey(connectionId)) {
			throw new JMSException("connection clientId " + connectionId + " not registered.");
		}
		connections.get(connectionId).registerSession(sessionId);
	}

	public void registerConsumer(String clientId, String sessionId, String consumerId, Destination destnation) throws JMSException {
		logger.debug("register jms consumer, consumerId =" + consumerId + ", sessionId =" + sessionId + " ,clientId = " + clientId);
		if (!connections.containsKey(clientId)) {
			throw new JMSException("connection clientId " + clientId + " not registered.");
		}
		SessionMeta sessionMeta = getSession(clientId, sessionId);
		ConsumerMeta meta = sessionMeta.registerCunsumer(consumerId);
		destinations.registerConsumer(destnation, meta);
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * �ͻ��˵��ø÷���������Ϣ
	 * ������˽��յ���Ϣ֮�󣬰����ô浽ÿһ��ע������Ϣ��ַ��Cosumer��
	 * �����͵�ʱ�򣬽�b����Ϣ�ĸ�������
	 *
	 * @param message
	 */
	public void sendMessage(JMSMessage message) throws JMSException {
		logger.debug("receive message: " + message);
		Destination destination = message.getJMSDestination();
		if (!destinations.isDestinationRegistered(destination)) {
			throw new InvalidDestinationException("Destination " + destination + " not exists.");
		}

		tempMessages.add((JMSMessage) message);

		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * �ͻ��˵��ø÷�����������Ϣ�����ڱ��������Commit
	 *
	 * @param messages
	 */
	public void sendMessageBatch(JMSMessage[] messages) throws JMSException {
		throw new JMSException("not support now!");
	}

	/**
	 * �ͻ��˵��ø÷����õ���Ϣ
	 *
	 * @param clientId
	 * @param sessionId
	 * @param consumerId
	 * @param timeout    0 forever; -1 noWait; >1 timeToWait
	 * @return
	 */
	public JMSMessage receiveMessage(String clientId, String sessionId, String consumerId, long timeout) throws JMSException {
		ConnectionMeta connMeta = getConnection(clientId);
		if (!connMeta.isStarted()) {
			throw new IllegalStateException("connection " + clientId + " not started, can't receive message.");
		}
		ConsumerMeta meta = getConsumer(clientId, sessionId, consumerId);
		JMSMessage message = meta.popMessage();
		while (message == null) {
			try {
				if (timeout == 0) {
					Thread.sleep(50L);
				} else if (timeout > 0) {
					Thread.sleep(timeout);
					timeout = -1; // end loop
				} else {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			message = meta.popMessage();
		}
		logger.debug("receive message " + message + ", clientId=" + clientId + ", sessionId=" + sessionId + ", consumerId=" + consumerId);
		return message;
	}

	public void acknowledge(String clientId, String sessionId, String consumerId, String messageId) throws JMSException {
		logger.debug("acknowledge message: messageId=" + messageId + ", clientId=" + clientId + ", sessionId=" + sessionId + ", consumerId=" + consumerId);
		ConsumerMeta meta = getConsumer(clientId, sessionId, consumerId);
		meta.acknowlege(messageId);
	}

	public void startConnection(String clientId) throws JMSException {
		logger.debug("startConnection clientId=" + clientId);
		ConnectionMeta connMeta = getConnection(clientId);
		connMeta.start();
	}

	public void stopConnection(String clientId) throws JMSException {
		logger.debug("stopConnection clientId=" + clientId);
		ConnectionMeta connMeta = getConnection(clientId);
		connMeta.stop();
	}

	public void setConsumerAsync(String clientId, String sessionId, String consumerId, boolean async) throws JMSException {
		ConsumerMeta meta = getConsumer(clientId, sessionId, consumerId);
		meta.setAsync(async);
	}

	public void closeSession(String clientId, String sessionId) throws JMSException {
		logger.debug("closeSession sessionId=" + sessionId + ", clientId=" + clientId);
		SessionMeta meta = getSession(clientId, sessionId);
		meta.close();
	}

	public void run() {
//		while (isStarted()) {
        while (true) {
            try {
				while (beWait()) {
					synchronized (this) {
						wait();
					}
				}
				Collections.sort(tempMessages);
				for (Iterator it = tempMessages.iterator(); it.hasNext();) {
//                for(JMSMessage message : tempMessages) {
					JMSMessage message = (JMSMessage) it.next();
					Destination destination = message.getJMSDestination();

					if (destinations.hashConsumer(destination)) {
						List<ConsumerMeta> consumers = destinations.getConsumerMetas(destination);
						boolean isQueue = (destination instanceof Queue) ? true : false;
						if (isQueue) {
							//�������һ��Consumer
							int index = rand.nextInt(consumers.size());
							ConsumerMeta meta = consumers.get(index);
							meta.addMessage(message);
							logger.debug("dispatch message " + message + " to consumer " + meta.getConsumerId());
						} else {
							//�����ÿһ��Consumer
							for (ConsumerMeta meta : consumers) {
								meta.addMessage(message);
							}
						}
						it.remove();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * �����߳��Ƿ����ȴ�״̬
	 * 1.û����Ϣ
	 * 2.����Ϣ���������е���Ϣ��û��consumer
	 *
	 * @return
	 * @throws JMSException
	 */
	private boolean beWait() throws JMSException {
		if (tempMessages.isEmpty()) {
			return true;
		} else {
			for (Message message : tempMessages) {
				Destination destination = message.getJMSDestination();
				if (destinations.hashConsumer(destination)) {
					return false;
				}
			}
			return true;
		}
	}

	private ConnectionMeta getConnection(String clientId) throws JMSException {
		if (!connections.containsKey(clientId)) {
			throw new JMSException("connection clientId " + clientId + " not registered.");
		}
		ConnectionMeta connMeta = connections.get(clientId);
		return connMeta;
	}

	private SessionMeta getSession(String clientId, String sessionId) throws JMSException {
		ConnectionMeta connMeta = getConnection(clientId);
		SessionMeta sessionMeta = connMeta.getSession(sessionId);
		if (sessionMeta == null) {
			throw new JMSException("no session " + sessionId + " in connection " + clientId);
		}
		return sessionMeta;
	}

	private ConsumerMeta getConsumer(String clientId, String sessionId, String consumerId) throws JMSException {
		SessionMeta sessionMeta = getSession(clientId, sessionId);
		ConsumerMeta consumerMeta = sessionMeta.getConsumer(consumerId);
		if (consumerMeta == null) {
			throw new JMSException("no consumer " + consumerId + " in session " + sessionId + " of connection " + clientId);
		}
		return consumerMeta;
	}

	public static void main(String[] args) {

	}

}
