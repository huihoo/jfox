/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package net.sourceforge.jfox.jms.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.JMSException;

import net.sourceforge.jfox.jms.message.JMSMessage;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */

public class ConsumerMeta {

	private String consumerId;

	private SessionMeta sessionMeta;

	private List<JMSMessage> messages = new ArrayList<JMSMessage>();
	private Map<String, JMSMessage> unackMessages = new HashMap<String, JMSMessage>();

	private boolean async = false;

	public ConsumerMeta(String consumerId, SessionMeta sessionMeta) {
		this.consumerId = consumerId;
		this.sessionMeta = sessionMeta;
	}

	public String getSessionId() {
		return sessionMeta.getSessionId();
	}

	public void addMessage(JMSMessage message) {
		messages.add(message);
		// ������첽���գ�֪ͨsessionMeta�̷߳�����Ϣ
		if (this.isAsync()) {
			synchronized (sessionMeta) {
				sessionMeta.notifyAll();
			}
		}
	}

	public synchronized JMSMessage popMessage() throws JMSException {
		Collections.sort(messages);
		JMSMessage message = null;
		while (!messages.isEmpty()) {
			JMSMessage msg = messages.remove(0);
			//��Ϣδ��ʱ
			if (msg.getJMSExpiration() == 0 || (System.currentTimeMillis() < msg.getJMSExpiration())) {
				message = msg;
				//����Ϣ��ӵ� unack ��
				unackMessages.put(message.getJMSMessageID(), message);
				break;
			}
		}

		return message;

	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setAsync(boolean isAsync) {
		this.async = isAsync;
	}

	public boolean isAsync() {
		return async;
	}

	public boolean hasMessage() {
		return !messages.isEmpty();
	}

	public void acknowlege(String messageId) throws JMSException {
		if (!unackMessages.containsKey(messageId)) {
			throw new JMSException("message " + messageId + " not exists.");
		}
		unackMessages.remove(messageId);
	}

	public static void main(String[] args) {

	}
}

