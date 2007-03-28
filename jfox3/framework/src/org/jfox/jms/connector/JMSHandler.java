/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.connector;


/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSHandler { // extends AbstractHandler {

/*
	public JMSHandler(Container container) {
		super(container);
	}
*/

	public Class getInvocationClass() {
		return JMSInvocation.class;
	}

	public static void main(String[] args) {

	}
}
