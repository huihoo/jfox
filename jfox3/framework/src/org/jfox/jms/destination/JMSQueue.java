/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSQueue extends JMSDestination implements Queue {

    //TODO: hold a destination container
    private transient QueueMessagePool msgPool = null ;

    public JMSQueue(String name) {
		super(name);
	}

	public String getQueueName() throws JMSException {
		return getName();
	}

	public boolean isTopic() {
		return false;
	}

	public static void main(String[] args) {

	}
}
