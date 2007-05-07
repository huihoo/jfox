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

import java.io.Serializable;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * <p/>
 * This class implements message header fields for messages.
 * </p>
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-11-10 20:02:32
 */

public class MessageHeader implements Serializable {

	private Destination destination = null;
	private int deliveryMode = DeliveryMode.NON_PERSISTENT;
	private String messageID = null;
	private long timestamp = 0;
	private String correlationID = "";
	private Destination replyTo = null;
	private boolean redelivered = false;
	private String messageType = "";
	private long expiration = 0;
	private int priority = 0;

	public final static String MESSAGEID_PREFIX = "ID:";

	/**
	 * Default Constructor without param
	 */
	MessageHeader() {
	}

	/**
	 * Store the destination associated with this message.
	 *
	 * @param destination The destination value to store (null is not valid).
	 * @throws javax.jms.JMSException if an error occurs while storing the value.
	 * @see javax.jms.Message
	 */
	public void setJMSDestination(Destination destination)
	        throws JMSException {
		if (destination instanceof Destination) {
			this.destination = (Destination) destination;
		} else {
			throw new JMSException("Message Destination unKnown.");
		}
	}

	/**
	 * The destination field contains the destination to which the message is
	 * being sent.
	 *
	 * @return The destination which the message is being delivered to (after a
	 *         send) or has been received from (after a receive).
	 * @throws javax.jms.JMSException an error occurred while retreiving the data.
	 */
	public Destination getJMSDestination() throws JMSException {
		return this.destination;
	}

	/**
	 * Store the delivery mode associated with this message. This value
	 * determines the persistence of a message and is set by a client prior to
	 * sending a message. This implements the javax.jms.Message function.
	 *
	 * @param deliveryMode The defaultDeliveryMode to store.
	 * @throws javax.jms.JMSException if an error occurs while storing the value.
	 * @see javax.jms.DeliveryMode
	 */
	public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
		if (deliveryMode != DeliveryMode.NON_PERSISTENT && deliveryMode != DeliveryMode.PERSISTENT) {
			throw new JMSException("invalide Delivery Mode: " + deliveryMode);
		}
		this.deliveryMode = deliveryMode;
	}

	/**
	 * Get the currently stored delivery mode.
	 *
	 * @return defaultDeliveryMode
	 * @throws javax.jms.JMSException if an error occurs while retrieve the value.
	 * @see javax.jms.DeliveryMode
	 */
	public int getJMSDeliveryMode() throws JMSException {
		return deliveryMode;
	}

	/**
	 * Sets the message ID.
	 * <p/>
	 * <P>
	 * JMS providers set this field when a message is sent. This method can be
	 * used to change the value for a message that has been received.
	 *
	 * @param id the ID of the message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message ID due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSMessageID()
	 */
	public void setJMSMessageID(String id) throws JMSException {
		if (!id.startsWith(MESSAGEID_PREFIX)) {
			throw new JMSException("The message id must start with ID:");
		}
		this.messageID = id;
	}

	/**
	 * Gets the message ID.
	 * <p/>
	 * <P>
	 * The <CODE>JMSMessageID</CODE> header field contains a value that
	 * uniquely identifies each message sent by a provider.
	 * <p/>
	 * <P>
	 * When a message is sent, <CODE>JMSMessageID</CODE> can be ignored. When
	 * the <CODE>send</CODE> or <CODE>publish</CODE> method returns, it
	 * contains a provider-assigned value.
	 * <p/>
	 * <P>
	 * A <CODE>JMSMessageID</CODE> is a <CODE>String</CODE> value that
	 * should function as a nique key for identifying messages in a historical
	 * repository.
	 * <p/>
	 * <P>
	 * All <CODE>JMSMessageID</CODE> values must start with the prefix <CODE>
	 * 'ID:'</CODE>. Uniqueness of message ID values across different
	 * providers is not required.
	 *
	 * @return the message ID
	 * @throws javax.jms.JMSException if the JMS provider fails to get the message ID due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSMessageID(String)
	 * @see javax.jms.MessageProducer#setDisableMessageID(boolean)
	 */
	public String getJMSMessageID() throws JMSException {
		return messageID;
/*
        if(this.messageID != null) {
            return this.messageID.getMessageID();
        }
        else {
            throw new JMSException("Message ID not initialized");
        }
*/
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
		this.timestamp = timestamp;
	}

	/**
	 * Gets the message timestamp.
	 * <p/>
	 * <P>
	 * The <CODE>JMSTimestamp</CODE> header field contains the time a message
	 * was handed off to a provider to be sent. It is not the time the message
	 * was actually transmitted, because the actual send may occur later due to
	 * transactions or other client-side queueing of messages.
	 *
	 * @return the message timestamp
	 * @throws javax.jms.JMSException if the JMS provider fails to get the timestamp due to
	 *                                some internal error.
	 * @see javax.jms.Message#setJMSTimestamp(long)
	 * @see javax.jms.MessageProducer#setDisableMessageTimestamp(boolean)
	 */
	public long getJMSTimestamp() throws JMSException {
		return timestamp;
	}

	/**
	 * Sets the correlation ID for the message.
	 * <p/>
	 * <P>
	 * A client can use the <CODE>JMSCorrelationID</CODE> header field to
	 * link one message with another. A typical use is to link a response
	 * message with its request message.
	 * <p/>
	 * <P>
	 * <CODE>JMSCorrelationID</CODE> can hold one of the following:
	 * <UL>
	 * <LI>A provider-specific message ID
	 * <LI>An application-specific <CODE>String</CODE>
	 * <LI>A provider-native <CODE>byte[]</CODE> value
	 * </UL>
	 * <p/>
	 * <P>
	 * Since each message sent by a JMS provider is assigned a message ID
	 * value, it is convenient to link messages via message ID. All message ID
	 * values must start with the <CODE>'ID:'</CODE> prefix.
	 * <p/>
	 * <P>
	 * In some cases, an application (made up of several clients) needs to use
	 * an application-specific value for linking messages. For instance, an
	 * application may use <CODE>JMSCorrelationID</CODE> to hold a value
	 * referencing some external information. Application-specified values must
	 * not start with the <CODE>'ID:'</CODE> prefix; this is reserved for
	 * provider-generated message ID values.
	 * <p/>
	 * <P>
	 * If a provider supports the native concept of correlation ID, a JMS
	 * client may need to assign specific <CODE>JMSCorrelationID</CODE>
	 * values to match those expected by clients that do not use the JMS API. A
	 * <CODE>byte[]</CODE> value is used for this purpose. JMS providers
	 * without native correlation ID values are not required to support <CODE>
	 * byte[]</CODE> values. The use of a <CODE>byte[]</CODE> value for
	 * <CODE>JMSCorrelationID</CODE> is non-portable.
	 *
	 * @param correlationID the message ID of a message being referred to
	 * @throws javax.jms.JMSException if the JMS provider fails to set the correlation ID due
	 *                                to some internal error.
	 * @see javax.jms.Message#getJMSCorrelationID()
	 * @see javax.jms.Message#getJMSCorrelationIDAsBytes()
	 * @see javax.jms.Message#setJMSCorrelationIDAsBytes(byte[])
	 */
	public void setJMSCorrelationID(String correlationID) throws JMSException {
		this.correlationID = correlationID;
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
		return correlationID;
	}

	/**
	 * Sets the correlation ID as an array of bytes for the message.
	 * <p/>
	 * <P>
	 * The array is copied before the method returns, so future modifications
	 * to the array will not alter this message header.
	 * <p/>
	 * <P>
	 * If a provider supports the native concept of correlation ID, a JMS
	 * client may need to assign specific <CODE>JMSCorrelationID</CODE>
	 * values to match those expected by native messaging clients. JMS
	 * providers without native correlation ID values are not required to
	 * support this method and its corresponding get method; their
	 * implementation may throw a <CODE>
	 * java.lang.UnsupportedOperationException</CODE>.
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
	void setJMSCorrelationIDAsBytes(byte[] correlationID)
	        throws JMSException {
		this.correlationID = new String(correlationID);
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
	byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		return correlationID.getBytes();
	}

	/**
	 * Sets the <CODE>Destination</CODE> object to which a reply to this
	 * message should be sent.
	 * <p/>
	 * <P>
	 * The <CODE>JMSReplyTo</CODE> header field contains the destination
	 * where a reply to the current message should be sent. If it is null, no
	 * reply is expected. The destination may be either a <CODE>Queue</CODE>
	 * object or a <CODE>Topic</CODE> object.
	 * <p/>
	 * <P>
	 * Messages sent with a null <CODE>JMSReplyTo</CODE> value may be a
	 * notification of some event, or they may just be some data the sender
	 * thinks is of interest.
	 * <p/>
	 * <P>
	 * Messages with a <CODE>JMSReplyTo</CODE> value typically expect a
	 * response. A response is optional; it is up to the client to decide.
	 * These messages are called requests. A message sent in response to a
	 * request is called a reply.
	 * <p/>
	 * <P>
	 * In some cases a client may wish to match a request it sent earlier with
	 * a reply it has just received. The client can use the <CODE>
	 * JMSCorrelationID</CODE> header field for this purpose.
	 *
	 * @param replyTo <CODE>Destination</CODE> to which to send a response to
	 *                this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the <CODE>JMSReplyTo
	 *                                </CODE> destination due to some internal error.
	 * @see javax.jms.Message#getJMSReplyTo()
	 */
	public void setJMSReplyTo(Destination replyTo) throws JMSException {
		if (replyTo instanceof Destination) {
			this.replyTo = (Destination) replyTo;
		} else {
			throw new JMSException("Illegal Destination Type.");
		}
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
		return this.replyTo;
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
		this.redelivered = redelivered;
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
		return this.redelivered;
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
		return messageType;
	}

	/**
	 * Sets the message type.
	 * <p/>
	 * <P>
	 * The JMS API does not define a standard message definition repository,
	 * nor does it define a naming policy for the definitions it contains.
	 * <p/>
	 * <P>
	 * Some messaging systems require that a message type definition for each
	 * application message be created and that each message specify its type.
	 * In order to work with such JMS providers, JMS clients should assign a
	 * value to <CODE>JMSType</CODE>, whether the application makes use of
	 * it or not. This ensures that the field is properly set for those
	 * providers that require it.
	 * <p/>
	 * <P>
	 * To ensure portability, JMS clients should use symbolic values for <CODE>
	 * JMSType</CODE> that can be configured at installation time to the
	 * values defined in the current provider's message repository. If string
	 * literals are used, they may not be valid type names for some JMS
	 * providers.
	 *
	 * @param type the message type
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message type due to
	 *                                some internal error.
	 * @see javax.jms.Message#getJMSType()
	 */
	public void setJMSType(String type) throws JMSException {
		this.messageType = type;
	}

	/**
	 * Sets the message's expiration value.
	 *
	 * @param expiration the message's expiration time
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message expiration
	 *                                due to some internal error.
	 * @see javax.jms.Message
	 */
	public void setJMSExpiration(long expiration) throws JMSException {
		if (expiration >= 0) {
			this.expiration = expiration;
		} else {
			throw new JMSException("Illegal Message Expiration.");
		}

	}

	/**
	 * Gets the message's expiration value.
	 * <p/>
	 * <P>
	 * When a message is sent, the <CODE>JMSExpiration</CODE> header field is
	 * left unassigned. After completion of the <CODE>send</CODE> or <CODE>
	 * publish</CODE> method, it holds the expiration time of the message.
	 * This is the sum of the time-to-live value specified by the client and
	 * the GMT at the time of the <CODE>send</CODE> or <CODE>publish</CODE>.
	 * <p/>
	 * <P>
	 * If the time-to-live is specified as zero, <CODE>JMSExpiration</CODE>
	 * is set to zero to indicate that the message does not expire.
	 * <p/>
	 * <P>
	 * When a message's expiration time is reached, a provider should discard
	 * it. The JMS API does not define any form of notification of message
	 * expiration.
	 * <p/>
	 * <P>
	 * Clients should not receive messages that have expired; however, the JMS
	 * API does not guarantee that this will not happen.
	 *
	 * @return the time the message expires, which is the sum of the
	 *         time-to-live value specified by the client and the GMT at the
	 *         time of the send
	 * @throws javax.jms.JMSException if the JMS provider fails to get the message expiration
	 *                                due to some internal error.
	 * @see javax.jms.Message
	 */
	public long getJMSExpiration() throws JMSException {
		return expiration;
	}

	/**
	 * Sets the defaultPriority level for this message.
	 *
	 * @param priority the defaultPriority of this message
	 * @throws javax.jms.JMSException if the JMS provider fails to set the message
	 *                                defaultPriority due to some internal error.
	 * @see javax.jms.Message
	 */
	public void setJMSPriority(int priority) throws JMSException {
		if (priority >= 0 && priority < 10) {
			this.priority = priority;
		} else {
			throw new JMSException("Invalid message defaultPriority");
		}
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
	 * @see javax.jms.Message
	 */
	public int getJMSPriority() throws JMSException {
		return priority;
	}
}
