/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */

/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.XAConnection;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueSession;
import javax.jms.XASession;
import javax.jms.XATopicConnection;
import javax.jms.XATopicSession;

/**
 * 
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSConnection implements Connection,
                                      QueueConnection,
                                      TopicConnection,
                                      XAConnection,
                                      XAQueueConnection,
                                      XATopicConnection {

	protected JMSConnectionFactory connectionFactory = null;
    protected boolean started = false;
    protected boolean closed = false;

    protected String clientId = null;

    protected boolean isXA = false;

	/*
	 * sessions created by this connection
	 * sessionId => session
	 */
    protected final transient Map<String, JMSSession> sessionMap = new HashMap<String, JMSSession>();

	public JMSConnection(String clientId, JMSConnectionFactory container, boolean isXA) {
		this.clientId = clientId;
		this.connectionFactory = container;
		this.isXA = isXA;
	}

	/**
	 * If a <CODE>Session</CODE> is transacted, the acknowledgement mode
	 * is ignored.
	 *
	 * @param transacted
	 * @param acknowledgeMode
	 * @return
	 * @throws JMSException
	 */
	public synchronized Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
		checkClosed();
		if (transacted) {
			acknowledgeMode = Session.SESSION_TRANSACTED;
		}
		JMSSession session = new JMSSession(this, transacted, acknowledgeMode, false);
		synchronized (sessionMap) {
			sessionMap.put(session.getSessionId(), session);
		}
		return session;
	}

	public String getClientID() throws JMSException {
		return clientId;
	}

	public void setClientID(String clientId) throws JMSException {
		checkClosed();
		if (started) {
			throw new IllegalStateException("connection has already started, can not set client identifier.");
		}
		throw new IllegalStateException("client identifier has already been setted by system.");
	}

	public ConnectionMetaData getMetaData() throws JMSException {
		throw new JMSException("not support now!");
	}

	public ExceptionListener getExceptionListener() throws JMSException {
		throw new JMSException("not support now!");
	}

	public void setExceptionListener(ExceptionListener listener) throws JMSException {
		throw new JMSException("not support now!");
	}

	/**
	 * start to receive messages
	 *
	 * @throws JMSException
	 */
	public synchronized void start() throws JMSException {
		if (!started) {
			this.started = true;
            for(JMSSession session : sessionMap.values()) {
                session.start();
            }
        }
	}

	public synchronized void stop() throws JMSException {
		if (started) {
			started = false;
		}
	}

	public synchronized void close() throws JMSException {
		if (closed) return;
		this.stop();
		closed = true;

		List<JMSSession> list = new ArrayList<JMSSession>(sessionMap.values());
		for (JMSSession session : list) {
			session.close();
		}

	}

	public ConnectionConsumer createConnectionConsumer(Destination destination,
	                                                   String messageSelector,
	                                                   ServerSessionPool sessionPool,
	                                                   int maxMessages) throws JMSException {
		checkClosed();
		throw new JMSException("not support now!");
	}

	public ConnectionConsumer createDurableConnectionConsumer(Topic topic,
	                                                          String subscriptionName,
	                                                          String messageSelector,
	                                                          ServerSessionPool sessionPool,
	                                                          int maxMessages) throws JMSException {
		checkClosed();
		throw new JMSException("not support now!");
	}

	public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
		return (QueueSession) createSession(transacted, acknowledgeMode);
	}

	public ConnectionConsumer createConnectionConsumer(Queue queue,
	                                                   String messageSelector,
	                                                   ServerSessionPool sessionPool,
	                                                   int maxMessages) throws JMSException {
		throw new JMSException("not support now!");
	}

	public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
		return (TopicSession) createSession(transacted, acknowledgeMode);
	}

	public ConnectionConsumer createConnectionConsumer(Topic topic,
	                                                   String messageSelector,
	                                                   ServerSessionPool sessionPool,
	                                                   int maxMessages) throws JMSException {
		throw new JMSException("not support now!");
	}

	public XASession createXASession() throws JMSException {
		checkClosed();
		if (!isXA) {
			throw new JMSException("current connection " + this + " is not an xa connection");
		}

		JMSSession session = new JMSSession(this, true, Session.SESSION_TRANSACTED, true);
		synchronized (sessionMap) {
			sessionMap.put(session.getSessionId(), session);
		}
		return session;
	}

	public XAQueueSession createXAQueueSession() throws JMSException {
		return (XAQueueSession) createXASession();
	}

	public XATopicSession createXATopicSession() throws JMSException {
		return (XATopicSession) createXASession();
	}


	JMSConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	protected void checkClosed() throws javax.jms.IllegalStateException {
		if (closed) {
			throw new javax.jms.IllegalStateException("connection closed");
		}
	}

	boolean isStarted() {
		return started;
	}

	void removeSession(String sessionId) throws JMSException {
        if(sessionMap.containsKey(sessionId)) {
            sessionMap.remove(sessionId);
        }
	}

	public static void main(String[] args) {

	}
}
