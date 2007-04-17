/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import java.io.Serializable;
import javax.jms.Destination;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

abstract class JMSDestination implements Destination, Serializable {
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

	protected abstract boolean isTopic();


	public static void main(String[] args) {

	}
}
