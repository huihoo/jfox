/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import java.io.Serializable;
import javax.jms.Destination;
import javax.jms.Message;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public abstract class JMSDestination implements Destination, Serializable {

	private String name;

    public JMSDestination(String name) {
		this.name = name;
	}

	public String toString() {
		return "JMSDestination [" + (isTopic() ? "Topic" : "Queue") + "] " + getName();
	}

	protected String getName() {
		return name;
	}

	public boolean equals(Object pObject) {
		if (this == pObject) return true;
		if (!(pObject instanceof JMSDestination)) return false;

		final JMSDestination jmsDestination = (JMSDestination) pObject;

		if (name != null ? !name.equals(jmsDestination.name) : jmsDestination.name != null) return false;

		return true;
	}

	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}

    public abstract boolean isTopic();

    public abstract void putMessage(Message msg);

    public abstract int remainSize();

    public abstract int sentSize();

    //TODO: 增加计数器，记录交换了多少消息

}
