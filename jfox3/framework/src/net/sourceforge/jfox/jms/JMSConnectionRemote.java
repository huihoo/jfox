/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.jms.JMSException;

import net.sourceforge.jfox.jms.message.JMSMessage;

/**
 * client connection is a remote object
 * <p/>
 * so server call call back when client receive message async
 *
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public interface JMSConnectionRemote extends Remote {

	/**
	 * the callback method when client receive message async
	 *
	 * @param msg
	 */
	void onMessage(String sessionId, String consumerId, JMSMessage msg) throws RemoteException, JMSException;

}
