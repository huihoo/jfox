/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms;

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnection;
import javax.jms.XATopicConnectionFactory;

import org.jfox.jms.connector.JMSContainer;


/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSConnectionFactory implements ConnectionFactory,
        QueueConnectionFactory,
        TopicConnectionFactory,
        XAConnectionFactory,
        XAQueueConnectionFactory,
        XATopicConnectionFactory,
        Serializable {

	private transient JMSContainer container = null;

	public Connection createConnection() throws JMSException {
		return createConnection(null, null);
	}

	public Connection createConnection(String userName, String password) throws JMSException {
		return createConnection(userName, password, false);
	}

	public QueueConnection createQueueConnection() throws JMSException {
		return (QueueConnection)createConnection();
	}

	public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        return (QueueConnection) createConnection(userName,password);
	}

	public TopicConnection createTopicConnection() throws JMSException {
        return (TopicConnection) createConnection();
	}

	public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        return (TopicConnection) createConnection(userName,password);
	}

	public XAConnection createXAConnection() throws JMSException {
		return createXAConnection(null, null);
	}

	public XAConnection createXAConnection(String userName, String password) throws JMSException {
		return createConnection(userName, password, true);
	}

	public XAQueueConnection createXAQueueConnection() throws JMSException {
		return (XAQueueConnection)createXAConnection();
	}

	public XAQueueConnection createXAQueueConnection(String userName, String password) throws JMSException {
        return (XAQueueConnection) createXAConnection(userName,password);
	}

	public XATopicConnection createXATopicConnection() throws JMSException {
        return (XATopicConnection) createXAConnection();
	}

	public XATopicConnection createXATopicConnection(String userName, String password) throws JMSException {
        return (XATopicConnection) createXAConnection(userName,password);
	}

	/**
	 * @return
	 * @throws JMSException
	 */
	protected synchronized JMSConnection createConnection(String userName, String password, boolean isXA) throws JMSException {

//		String clientId = UUID.randomUUID().toString();
/* comment for compiler
		ObjectId clientId = new ObjectId(ObjectUUID.randomUUID());
		if (container == null) {
			// lookup ConnectorService from jndi, 鐒堕敓鏂ゆ嫹浣块敓鐭鎷锋€侀敓鏂ゆ嫹閿熸枻鎷烽敓闃跺府鎷烽敓?JMSContainer 閿熸枻鎷烽敓鏂ゆ嫹
			ConnectorRemote remote = ConnectorHelper.lookupConnector(JNDIProperties.getProviderURL());
			container = (JMSContainer) Proxy.newProxyInstance(this.getClass().getClassLoader(),
			        new Class[]{JMSContainer.class},
			        new JMSConnectorInvoker(clientId, remote));
		}

		container.auth(userName, password);

		JMSConnection conn = new JMSConnection(clientId.toString(), container, isXA);
		try {
			UnicastRemoteObject.exportObject(conn);
			container.registerConnection(clientId.toString(), Marshaller.marshall(conn));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
*/
        return null;
    }

	public static void main(String[] args) {

	}
}