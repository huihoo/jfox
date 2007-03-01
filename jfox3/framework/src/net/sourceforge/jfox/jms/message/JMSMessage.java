/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms.message;

import java.io.Serializable;
import java.util.Enumeration;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;

import net.sourceforge.jfox.jms.JMSConsumer;
import net.sourceforge.jfox.jms.JMSSession;

/**
 * <p/>
 * This class implements the JMS Message. The message class is the underlying
 * object for all the JMS message items. This message class provides the basic
 * functionality for message processing including header items and properties.
 * </p>
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-12-01 22:09:17
 */

public class JMSMessage implements Message, Serializable, Comparable {

	private MessageHeader messageHeader = new MessageHeader();
	private MessageProperties messageProperties = new MessageProperties();

	// true if message properties are modifiable, false if message properties
	private boolean propertiesModifiable = true;

	// true if message body is modifiable, false if message body is read-only
	private boolean bodyModifiable = true;

	/**
	 * ��Ϣ���յ�session
	 */
	private transient JMSSession session = null;
	/**
	 * ��Ϣ���յ�consumer
	 */
	private transient JMSConsumer consumer = null;

	public JMSMessage() {
		try {
			setJMSPriority(DEFAULT_PRIORITY);
			setJMSDeliveryMode(DEFAULT_DELIVERY_MODE);
		} catch (JMSException e) {
		}

		try {
			clearProperties();
		} catch (JMSException je) {
		}
		setBodyModifiable(true);
	}

	/**
	 * Gets the message ID.
	 *
	 * @return messageID
	 * @throws javax.jms.JMSException if the JMS provider fails to get the message ID due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSMessageID(String)
	 * @see javax.jms.MessageProducer#setDisableMessageID(boolean)
	 */
	public String getJMSMessageID() throws JMSException {
		return messageHeader.getJMSMessageID();
	}

	/**
	 * Sets the message ID.
	 *
	 * @param id the ID of the message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message ID due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSMessageID()
	 */
	public void setJMSMessageID(String id) throws JMSException {
		messageHeader.setJMSMessageID(id);
	}

	/**
	 * Gets the message timestamp.
	 *
	 * @return the message timestamp
	 * @throws javax.jms.JMSException if the JMS provider fails to get the timestamp due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSTimestamp(long)
	 * @see javax.jms.MessageProducer#setDisableMessageTimestamp(boolean)
	 */
	public long getJMSTimestamp() throws JMSException {
		return messageHeader.getJMSTimestamp();
	}

	/**
	 * Sets the message timestamp.
	 * <p/>
	 * <P>
	 * JMS providers set this field when a message is sent. This method can be
	 * used to change the value for a message that has been received.
	 *
	 * @param timestamp the timestamp for this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the timestamp due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSTimestamp()
	 */
	public void setJMSTimestamp(long timestamp) throws JMSException {
		messageHeader.setJMSTimestamp(timestamp);
	}

	/**
	 * Gets the correlation ID as an array of bytes for the message.
	 * <p/>
	 * <P>
	 * The use of a <CODE>byte[]</CODE> value for <CODE>JMSCorrelationID
	 * </CODE> is non-portable.
	 *
	 * @return the correlation ID of a message as an array of bytes
	 * @throws javax.jms.JMSException if the JMS provider fails to get the correlation ID due
	 *                                to some internal error.
	 * @see javax.jms.Message#setJMSCorrelationID(String)
	 * @see javax.jms.Message#getJMSCorrelationID()
	 * @see javax.jms.Message#setJMSCorrelationIDAsBytes(byte[])
	 */
	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		return messageHeader.getJMSCorrelationIDAsBytes();
	}

	/**
	 * Sets the correlation ID as an array of bytes for the message.
	 * <p/>
	 * <P>
	 * The array is copied before the method returns, so future modifications
	 * to the array will not alter this message header.
	 * <p/>
	 * <P>
	 * The use of a <CODE>byte[]</CODE> value for <CODE>JMSCorrelationID
	 * </CODE> is non-portable.
	 *
	 * @param correlationID the correlation ID value as an array of bytes
	 * @throws javax.jms.JMSException if the JMS provider fails to set the correlation ID due
	 *                                to some internal error.
	 * @see javax.jms.Message#setJMSCorrelationID(String)
	 * @see javax.jms.Message#getJMSCorrelationID()
	 * @see javax.jms.Message#getJMSCorrelationIDAsBytes()
	 */
	public void setJMSCorrelationIDAsBytes(byte[] correlationID)
	        throws JMSException {
		messageHeader.setJMSCorrelationIDAsBytes(correlationID);
	}

	/**
	 * Gets the correlation ID for the message.
	 * <p/>
	 * <P>
	 * This method is used to return correlation ID values that are either
	 * provider-specific message IDs or application-specific <CODE>String
	 * </CODE> values.
	 *
	 * @return the correlation ID of a message as a <CODE>String</CODE>
	 * @throws javax.jms.JMSException if the JMS provider fails to get the correlation ID due
	 *                                to some internal error.
	 * @see javax.jms.Message#setJMSCorrelationID(String)
	 * @see javax.jms.Message#getJMSCorrelationIDAsBytes()
	 * @see javax.jms.Message#setJMSCorrelationIDAsBytes(byte[])
	 */
	public String getJMSCorrelationID() throws JMSException {
		return messageHeader.getJMSCorrelationID();
	}

	/**
	 * Sets the correlation ID for the message.
	 *
	 * @param correlationID the message ID of a message being referred to
	 * @throws javax.jms.JMSException if the JMS provider fails to set the correlation ID due
	 *                                to some internal error.
	 * @see javax.jms.Message#getJMSCorrelationID()
	 * @see javax.jms.Message#getJMSCorrelationIDAsBytes()
	 * @see javax.jms.Message#setJMSCorrelationIDAsBytes(byte[])
	 */
	public void setJMSCorrelationID(String correlationID) throws JMSException {
		messageHeader.setJMSCorrelationID(correlationID);
	}

	/**
	 * Gets the <CODE>Destination</CODE> object to which a reply to this
	 * message should be sent.
	 *
	 * @return <CODE>Destination</CODE> to which to send a response to this
	 *         message
	 * @throws javax.jms.JMSException if the JMS provider fails to get the <CODE>JMSReplyTo
	 *                                </CODE> destination due to some internal error.
	 * @see javax.jms.Message#setJMSReplyTo(javax.jms.Destination)
	 */
	public Destination getJMSReplyTo() throws JMSException {
		return messageHeader.getJMSReplyTo();
	}

	/**
	 * Sets the <CODE>Destination</CODE> object to which a reply to this
	 * message should be sent.
	 *
	 * @param replyTo <CODE>Destination</CODE> to which to send a response to
	 *                this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the <CODE>JMSReplyTo
	 *                                </CODE> destination due to some internal error.
	 * @see javax.jms.Message#getJMSReplyTo()
	 */
	public void setJMSReplyTo(Destination replyTo) throws JMSException {
		messageHeader.setJMSReplyTo(replyTo);
	}

	/**
	 * Gets the <CODE>Destination</CODE> object for this message.
	 *
	 * @return the destination of this message
	 * @throws javax.jms.JMSException if the JMS provider fails to get the destination due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSDestination(javax.jms.Destination)
	 */
	public Destination getJMSDestination() throws JMSException {
		return messageHeader.getJMSDestination();
	}

	/**
	 * Sets the <CODE>Destination</CODE> object for this message.
	 * <p/>
	 * <P>
	 * JMS providers set this field when a message is sent. This method can be
	 * used to change the value for a message that has been received.
	 *
	 * @param destination the destination for this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the destination due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSDestination()
	 */
	public void setJMSDestination(Destination destination)
	        throws JMSException {
		messageHeader.setJMSDestination(destination);
	}

	/**
	 * Gets the <CODE>DeliveryMode</CODE> value specified for this message.
	 *
	 * @return the delivery mode for this message
	 * @throws javax.jms.JMSException if the JMS provider fails to get the delivery mode due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSDeliveryMode(int)
	 * @see javax.jms.DeliveryMode
	 */
	public int getJMSDeliveryMode() throws JMSException {
		return messageHeader.getJMSDeliveryMode();
	}

	/**
	 * Sets the <CODE>DeliveryMode</CODE> value for this message.
	 * <p/>
	 * <P>
	 * JMS providers set this field when a message is sent. This method can be
	 * used to change the value for a message that has been received.
	 *
	 * @param deliveryMode the delivery mode for this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the delivery mode due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSDeliveryMode()
	 * @see javax.jms.DeliveryMode
	 */
	public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
		messageHeader.setJMSDeliveryMode(deliveryMode);
	}

	/**
	 * Gets an indication of whether this message is being redelivered.
	 * <p/>
	 * <P>
	 * If a client receives a message with the <CODE>JMSRedelivered</CODE>
	 * field set, it is likely, but not guaranteed, that this message was
	 * delivered earlier but that its receipt was not acknowledged at that
	 * time.
	 *
	 * @return true if this message is being redelivered
	 * @throws javax.jms.JMSException if the JMS provider fails to get the redelivered state
	 *                                due to some internal error.
	 * @see javax.jms.Message#setJMSRedelivered(boolean)
	 */
	public boolean getJMSRedelivered() throws JMSException {
		return messageHeader.getJMSRedelivered();
	}

	/**
	 * Specifies whether this message is being redelivered.
	 * <p/>
	 * <P>
	 * This field is set at the time the message is delivered. This method can
	 * be used to change the value for a message that has been received.
	 *
	 * @param redelivered an indication of whether this message is being redelivered
	 * @throws javax.jms.JMSException if the JMS provider fails to set the redelivered state
	 *                                due to some internal error.
	 * @see javax.jms.Message#getJMSRedelivered()
	 */
	public void setJMSRedelivered(boolean redelivered) throws JMSException {
		messageHeader.setJMSRedelivered(redelivered);
	}

	/**
	 * Gets the message type identifier supplied by the client when the message
	 * was sent.
	 *
	 * @return the message type
	 * @throws javax.jms.JMSException if the JMS provider fails to get the message type due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSType(String)
	 */
	public String getJMSType() throws JMSException {
		return messageHeader.getJMSType();
	}

	/**
	 * Sets the message type.
	 *
	 * @param type the message type
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message type due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSType()
	 */
	public void setJMSType(String type) throws JMSException {
		messageHeader.setJMSType(type);
	}

	/**
	 * Gets the message's expiration value.
	 *
	 * @return the time the message expires, which is the sum of the
	 *         time-to-live value specified by the client and the GMT at the
	 *         time of the send
	 * @throws javax.jms.JMSException if the JMS provider fails to get the message expiration
	 *                                due to some internal error.
	 * @see javax.jms.Message#setJMSExpiration(long)
	 */
	public long getJMSExpiration() throws JMSException {
		return messageHeader.getJMSExpiration();
	}

	/**
	 * Sets the message's expiration value.
	 * <p/>
	 * <P>
	 * JMS providers set this field when a message is sent. This method can be
	 * used to change the value for a message that has been received.
	 *
	 * @param expiration the message's expiration time
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message expiration
	 *                                due to some internal error.
	 * @see javax.jms.Message#getJMSExpiration()
	 */
	public void setJMSExpiration(long expiration) throws JMSException {
		messageHeader.setJMSExpiration(expiration);
	}

	/**
	 * Gets the message defaultPriority level.
	 * <p/>
	 * <P>
	 * The JMS API defines ten levels of defaultPriority value, with 0 as the
	 * lowest defaultPriority and 9 as the highest. In addition, clients should
	 * consider priorities 0-4 as gradations of normal defaultPriority and
	 * priorities 5-9 as gradations of expedited defaultPriority.
	 *
	 * @return the default message defaultPriority
	 * @throws javax.jms.JMSException if the JMS provider fails to get the message
	 *                                defaultPriority due to some internal error.
	 * @see javax.jms.Message#setJMSPriority(int)
	 */
	public int getJMSPriority() throws JMSException {
		return messageHeader.getJMSPriority();
	}

	/**
	 * Sets the defaultPriority level for this message.
	 * <p/>
	 * <P>
	 * JMS providers set this field when a message is sent. This method can be
	 * used to change the value for a message that has been received.
	 *
	 * @param priority the defaultPriority of this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message
	 *                                defaultPriority due to some internal error.
	 * @see javax.jms.Message#getJMSPriority()
	 */
	public void setJMSPriority(int priority) throws JMSException {
		messageHeader.setJMSPriority(priority);
	}

	/**
	 * Clears a message's properties.
	 *
	 * @throws javax.jms.JMSException if an error occurs while clearing the properties.
	 * @see javax.jms.Message#clearProperties()
	 */
	public void clearProperties() throws JMSException {
		messageProperties.clearProperties();
		propertiesModifiable = true;
		bodyModifiable = true;
	}

	/**
	 * Indicates whether a property value exists.
	 *
	 * @throws javax.jms.JMSException if an error occurs while clearing the properties.
	 * @see javax.jms.Message#propertyExists(String)
	 */
	public boolean propertyExists(String name) throws JMSException {
		return messageProperties.propertyExists(name);
	}

	/**
	 * Returns the value of the <CODE>boolean</CODE> property with the
	 * specified name.
	 *
	 * @param name the name of the <CODE>boolean</CODE> property
	 * @return the <CODE>boolean</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public boolean getBooleanProperty(String name) throws JMSException {
		return messageProperties.getBooleanProperty(name);
	}

	/**
	 * Returns the value of the <CODE>byte</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>byte</CODE> property
	 * @return the <CODE>byte</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public byte getByteProperty(String name) throws JMSException {
		return messageProperties.getByteProperty(name);
	}

	/**
	 * Returns the value of the <CODE>short</CODE> property with the
	 * specified name.
	 *
	 * @param name the name of the <CODE>short</CODE> property
	 * @return the <CODE>short</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public short getShortProperty(String name) throws JMSException {
		return messageProperties.getShortProperty(name);
	}

	/**
	 * Returns the value of the <CODE>int</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>int</CODE> property
	 * @return the <CODE>int</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public int getIntProperty(String name) throws JMSException {
		return messageProperties.getIntProperty(name);
	}

	/**
	 * Returns the value of the <CODE>long</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>long</CODE> property
	 * @return the <CODE>long</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public long getLongProperty(String name) throws JMSException {
		return messageProperties.getLongProperty(name);
	}

	/**
	 * Returns the value of the <CODE>float</CODE> property with the
	 * specified name.
	 *
	 * @param name the name of the <CODE>float</CODE> property
	 * @return the <CODE>float</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public float getFloatProperty(String name) throws JMSException {
		return messageProperties.getFloatProperty(name);
	}

	/**
	 * Returns the value of the <CODE>double</CODE> property with the
	 * specified name.
	 *
	 * @param name the name of the <CODE>double</CODE> property
	 * @return the <CODE>double</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public double getDoubleProperty(String name) throws JMSException {
		return messageProperties.getDoubleProperty(name);
	}

	/**
	 * Returns the value of the <CODE>String</CODE> property with the
	 * specified name.
	 *
	 * @param name the name of the <CODE>String</CODE> property
	 * @return the <CODE>String</CODE> property value for the specified name;
	 *         if there is no property by this name, a null value is returned
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public String getStringProperty(String name) throws JMSException {
		return messageProperties.getStringProperty(name);
	}

	/**
	 * Returns the value of the Java object property with the specified name.
	 * <p/>
	 * <P>
	 * This method can be used to return, in objectified format, an object that
	 * has been stored as a property in the message with the equivalent <CODE>
	 * setObjectProperty</CODE> method call, or its equivalent primitive
	 * <CODE>set <I>type</I> Property</CODE> method.
	 *
	 * @param name the name of the Java object property
	 * @return the Java object property value with the specified name, in
	 *         objectified format (for example, if the property was set as an
	 *         <CODE>int</CODE>, an <CODE>Integer</CODE> is returned); if
	 *         there is no property by this name, a null value is returned
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property value due
	 *                                to some internal error.
	 */
	public Object getObjectProperty(String name) throws JMSException {
		return messageProperties.getObjectProperty(name);
	}

	/**
	 * Returns an <CODE>Enumeration</CODE> of all the property names.
	 * <p/>
	 * <P>
	 * Note that JMS standard header fields are not considered properties and
	 * are not returned in this enumeration.
	 *
	 * @return an enumeration of all the names of property values
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property names due
	 *                                to some internal error.
	 */
	public Enumeration getPropertyNames() throws JMSException {
		return messageProperties.getPropertyNames();
	}

	/**
	 * Sets a <CODE>boolean</CODE> property value with the specified name
	 * into the message.
	 *
	 * @param name  the name of the <CODE>boolean</CODE> property
	 * @param value the <CODE>boolean</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setBooleanProperty(String name, boolean value)
	        throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setBooleanProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message boolean property is read-only");
		}
	}

	/**
	 * Sets a <CODE>byte</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>byte</CODE> property
	 * @param value the <CODE>byte</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setByteProperty(String name, byte value) throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setByteProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message byte property is read-only");
		}
	}

	/**
	 * Sets a <CODE>short</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>short</CODE> property
	 * @param value the <CODE>short</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setShortProperty(String name, short value)
	        throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setShortProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message short property is read-only");
		}
	}

	/**
	 * Sets an <CODE>int</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>int</CODE> property
	 * @param value the <CODE>int</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setIntProperty(String name, int value) throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setIntProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message int property is read-only");
		}
	}

	/**
	 * Sets a <CODE>long</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>long</CODE> property
	 * @param value the <CODE>long</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setLongProperty(String name, long value) throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setLongProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message long property is read-only");
		}
	}

	/**
	 * Sets a <CODE>float</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>float</CODE> property
	 * @param value the <CODE>float</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setFloatProperty(String name, float value)
	        throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setFloatProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message float property is read-only");
		}
	}

	/**
	 * Sets a <CODE>double</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>double</CODE> property
	 * @param value the <CODE>double</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setDoubleProperty(String name, double value)
	        throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setDoubleProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message double property is read-only");
		}
	}

	/**
	 * Sets a <CODE>String</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>String</CODE> property
	 * @param value the <CODE>String</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setStringProperty(String name, String value)
	        throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setStringProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message string property is read-only");
		}
	}

	/**
	 * Sets a Java object property value with the specified name into the
	 * message.
	 * <p/>
	 * <P>
	 * Note that this method works only for the objectified primitive object
	 * types (<CODE>Integer</CODE>,<CODE>Double</CODE>,<CODE>Long
	 * </CODE> ...) and <CODE>String</CODE> objects.
	 *
	 * @param name  the name of the Java object property
	 * @param value the Java object property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if the object is invalid
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if properties are read-only
	 */
	public void setObjectProperty(String name, Object value)
	        throws JMSException {
		if (isPropertiesModifiable()) {
			messageProperties.setObjectProperty(name, value);
		} else {
			throw new MessageNotWriteableException("Message object property is read-only");
		}
	}

	/**
	 * @see javax.jms.Message#acknowledge()
	 */
	public void acknowledge() throws JMSException {
		if (session == null || consumer == null) {
			throw new JMSException("message not delivered.");
		}
		session.acknowledge(consumer, this);
	}

	/**
	 * Clears out the message body. Clearing a message's body does not clear
	 * its header values or property entries.
	 * <p/>
	 * <P>
	 * If this message body was read-only, calling this method leaves the
	 * message body in the same state as an empty body in a newly created
	 * message.
	 *
	 * @throws javax.jms.JMSException if the JMS provider fails to clear the message body due
	 *                                to some internal error.
	 */
	public void clearBody() throws JMSException {
		setBodyModifiable(true);
	}

	/**
	 * Set the read only state of the message properties.
	 *
	 * @param state false if the message properties are read-only, true if the
	 *              message properties can be modified.
	 */
	public void setPropertiesModifiable(boolean state) {
		propertiesModifiable = state;
	}

	/**
	 * Return true if the message properties are modifiable, else it is in
	 * read-only mode.
	 *
	 * @return true if message properties can be modified, false if not
	 */
	public boolean isPropertiesModifiable() {
		return propertiesModifiable;
	}

	/**
	 * Set the read only state of teh message body.
	 *
	 * @param state false if the message body are read-only, true if the message
	 *              body can be modified.
	 */
	public void setBodyModifiable(boolean state) {
		bodyModifiable = state;
	}

	/**
	 * Return true if the message body is modifiable, else it is in read-only
	 * mode.
	 *
	 * @return true if message body can be modified, false if not
	 */
	public boolean isBodyModifiable() {
		return bodyModifiable;
	}

	public int compareTo(Object pObject) {
		if (pObject == null || !(pObject instanceof Message)) return 0;
		try {
			return this.getJMSPriority() - ((Message) pObject).getJMSPriority();
		} catch (Exception e) {
			return 0;
		}
	}

	public void setSession(JMSSession session) {
		this.session = session;
	}

	public void setConsumer(JMSConsumer consumer) {
		this.consumer = consumer;
	}
}