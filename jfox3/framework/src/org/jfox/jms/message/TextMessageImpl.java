/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.message;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;
import javax.jms.TextMessage;

/**
 * <p/>
 * This class provides an implementation of the JMS TextMessage. This message
 * type is used to send a textual string to another java client.This message
 * can be used to transmit any valid java string including XML.
 * </p>
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-11-30 15:45:25
 */

public class TextMessageImpl extends JMSMessage implements TextMessage {

	private String text = null;

	/**
	 * Defalut contructor.
	 */
	public TextMessageImpl() {
		super();
	}

	/**
	 * Set TextMessage body
	 *
	 * @param string
	 * @throws javax.jms.JMSException if the JMS provider fails to set the TextMessage bodyt
	 *                                due to some internal error.
	 * @see javax.jms.TextMessage#setText(String)
	 */
	public void setText(String string) throws JMSException {
		if (isBodyModifiable()) {
			text = new String(string);
		} else {
			throw new MessageNotWriteableException("TextMessage is read-only.");
		}
	}

	/**
	 * Return TextMessage body
	 *
	 * @return text
	 * @see javax.jms.TextMessage#getText()
	 */
	public String getText() throws JMSException {
		return text;
	}

	/**
	 * Clear out the message body.
	 *
	 * @throws javax.jms.JMSException
	 */
	public void clearBody() throws JMSException {
		super.clearBody();
		text = null;
	}

	public String toString() {
		return "TextMessage {body=" + text + "}";
	}
}
