/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.jfox.jms.connector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jfox.jms.message.JMSMessage;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public class SessionMeta implements Runnable {
	private String sessionId;
	private ConnectionMeta connectionMeta;
	private Map<String, ConsumerMeta> consumers = new HashMap<String, ConsumerMeta>();

	private boolean closed = false;

	public SessionMeta(final String sessionId, ConnectionMeta connMeta) {
		this.sessionId = sessionId;
		this.connectionMeta = connMeta;
		//һ�� Session ��һ���̸߳������첽��Ϣ
		new Thread(this, "Session - " + sessionId + " Async Sender").start();
	}

	public String getConnectionId() {
		return connectionMeta.getConnectionId();
	}

	public ConsumerMeta registerCunsumer(String consumerId) {
		ConsumerMeta consumerMeta = new ConsumerMeta(consumerId, this);
		consumers.put(consumerId, consumerMeta);
		return consumerMeta;
	}

	public ConsumerMeta getConsumer(String consumerId) {
		return consumers.get(consumerId);
	}

	public void unregisterConsumer(String consumerId) {
		consumers.remove(consumerId);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void close() {
		closed = true;
		connectionMeta.unregisterSession(sessionId);
		synchronized (this) {
			notifyAll();
		}
	}

	public void run() {

		while (!closed) {
			try {
				if (beWaiting()) {
					synchronized (this) {
						wait();
					}
					if (closed) break;
				}
				for (Iterator it = consumers.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, ConsumerMeta> entry = (Map.Entry<String, ConsumerMeta>) it.next();
					String consumerId = entry.getKey();
					ConsumerMeta meta = entry.getValue();
					if (meta.isAsync()) {
						JMSMessage msg = meta.popMessage();
						if (msg != null) {
							connectionMeta.getJMSConnection().onMessage(sessionId, consumerId, msg);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private boolean beWaiting() {
		if (!connectionMeta.isStarted() || consumers.isEmpty()) {
			return true;
		} else {
			for (ConsumerMeta meta : consumers.values()) {
				if (meta.isAsync() && meta.hasMessage()) {
					return false;
				}
			}
			return true;
		}
	}


	public static void main(String[] args) {

	}
}

