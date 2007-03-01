/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package net.sourceforge.jfox.jms.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jfox.jms.JMSConnectionRemote;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */

public class ConnectionMeta {

	private String connectionId;
	private JMSConnectionRemote conn;

	private boolean started = false;

	private Map<String, SessionMeta> sessions = new HashMap<String, SessionMeta>();

	public ConnectionMeta(String connectionId, JMSConnectionRemote conn) {
		this.connectionId = connectionId;
		this.conn = conn;
	}

	public void registerSession(String sessionId) {
		SessionMeta sessionMeta = new SessionMeta(sessionId, this);
		sessions.put(sessionId, sessionMeta);
	}

	public SessionMeta getSession(String sessionId) {
		return sessions.get(sessionId);
	}

	public void unregisterSession(String sessionId) {
		sessions.remove(sessionId);
	}

	public JMSConnectionRemote getJMSConnection() {
		return conn;
	}

	public boolean isStarted() {
		return started;
	}

	public void start() {
		this.started = true;
		for (SessionMeta meta : sessions.values()) {
			synchronized (meta) {
				meta.notifyAll();
			}
		}
	}

	public void stop() {
		this.started = false;
	}

	public String getConnectionId() {
		return connectionId;
	}

	//TODO: close
	public void close() {
		List<SessionMeta> list = new ArrayList<SessionMeta>(sessions.values());
		for (SessionMeta session : list) {
			session.close();
		}
	}

	public static void main(String[] args) {

	}
}

