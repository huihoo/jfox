/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import javax.jms.JMSException;
import javax.jms.Topic;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSTopic extends JMSDestination implements Topic {

	public JMSTopic(String name) {
		super(name);
	}

	public String getTopicName() throws JMSException {
		return getName();
	}

	public boolean isTopic() {
		return true;
	}

	public static void main(String[] args) {

	}
}
