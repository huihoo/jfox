/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */

/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;
import javax.jms.ObjectMessage;

/**
 * <p/>
 * This class provides an implementation of the JMS ObjectMessage. An <CODE>
 * ObjectMessage</CODE> object is used to send a message that contains a
 * serializable object in the Java programming language ("Java object"). It
 * inherits from the <CODE>Message</CODE> interface and adds a body
 * containing a single reference to an object. Only <CODE>Serializable</CODE>
 * Java objects can be used.
 * </p>
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-12-08 11:25:10
 */

public class ObjectMessageImpl
        extends JMSMessage
        implements ObjectMessage, Serializable {

	private byte[] body;

	/**
	 * Default constructor
	 */
	public ObjectMessageImpl() {
		super();
	}

	/**
	 * Sets the serializable object containing this message's data. It is
	 * important to note that an <CODE>ObjectMessage</CODE> contains a
	 * snapshot of the object at the time <CODE>setObject()</CODE> is called;
	 * subsequent modifications of the object will have no effect on the <CODE>
	 * ObjectMessage</CODE> body.
	 *
	 * @param object the message's data
	 * @throws javax.jms.JMSException if the JMS provider fails to set the object due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if object serialization fails.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.ObjectMessage#setObject(java.io.Serializable)
	 */
	public void setObject(Serializable object) throws JMSException {
		if (isBodyModifiable()) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				oos.flush();
				oos.close();
				body = baos.toByteArray();
			} catch (InvalidClassException e) {
				throw new MessageFormatException(e.getMessage());
			} catch (NotSerializableException e) {
				throw new MessageFormatException(e.getMessage());
			} catch (IOException e) {
				throw new MessageFormatException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("ObjectMessage body read_only");
		}
	}

	/**
	 * Gets the serializable object containing this message's data. The default
	 * value is null.
	 *
	 * @return the serializable object containing this message's data
	 * @throws javax.jms.JMSException if the JMS provider fails to get the object due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if object deserialization fails.
	 * @see javax.jms.ObjectMessage#getObject()
	 */
	public Serializable getObject() throws JMSException {
		Serializable object = null;

		if (body != null) {
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(body);
				ObjectInputStream ins = new ObjectInputStream(bais);
				object = (Serializable) ins.readObject();
				ins.close();
			} catch (ClassNotFoundException e) {
				throw new MessageFormatException(e.getMessage());
			} catch (InvalidClassException e) {
				throw new MessageFormatException(e.getMessage());
			} catch (IOException e) {
				throw new MessageFormatException(e.getMessage());
			}
		}
		return object;
	}

	/**
	 * Sets the object reference in the message to null and toggles the message
	 * to read/write state.
	 *
	 * @throws javax.jms.JMSException
	 */
	public void clearBody() throws JMSException {
		super.clearBody();
		body = null;
	}
}
