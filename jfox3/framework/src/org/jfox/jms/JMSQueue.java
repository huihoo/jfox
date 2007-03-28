/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSQueue extends JMSDestination implements Queue {

	public JMSQueue(String name) {
		super(name);
	}

	public String getQueueName() throws JMSException {
		return getName();
	}

	protected boolean isTopic() {
		return false;
	}

	public static void main(String[] args) {

	}
}
