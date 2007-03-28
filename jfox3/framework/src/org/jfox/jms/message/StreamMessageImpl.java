/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import javax.jms.StreamMessage;


/**
 * <p/>
 * A <CODE>BytesMessage</CODE> object is used to send a message containing a
 * stream of uninterpreted bytes. It inherits from the <CODE>Message</CODE>
 * interface and adds a bytes message body. The receiver of the message
 * supplies the interpretation of the bytes.
 * </p>
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-12-08 22:46:11
 */

public class StreamMessageImpl
        extends JMSMessage
        implements StreamMessage, Serializable {

	public static final byte TYPE_NULL = 0;
	public static final byte TYPE_BOOLEAN = 1;
	public static final byte TYPE_BYTE = 2;
	public static final byte TYPE_BYTE_ARRAY = 3;
	public static final byte TYPE_SHORT = 4;
	public static final byte TYPE_CHAR = 5;
	public static final byte TYPE_INT = 6;
	public static final byte TYPE_LONG = 7;
	public static final byte TYPE_FLOAT = 8;
	public static final byte TYPE_DOUBLE = 9;
	public static final byte TYPE_STRING = 10;

	private transient DataOutputStream dos = null;
	private transient DataInputStream dis = null;
	private transient ByteArrayOutputStream baos = null;
	private transient ByteArrayInputStream bais = null;
	private byte[] buffer = null;
	private int readBytes = 0;
	private int byteArrayLength = 0;

	private static final String[] TYPE_NAMES =
	        {
		        "null",
		        "boolean",
		        "byte",
		        "byte[]",
		        "short",
		        "char",
		        "int",
		        "long",
		        "float",
		        "double",
		        "String"};

	/**
	 * Default constructor.
	 */
	public StreamMessageImpl() {
		super();
		buffer = new byte[0];
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
	}

	/**
	 * Reads a <code>boolean</code> from the stream message.
	 *
	 * @return the <code>boolean</code> value read
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readBoolean()
	 */
	public boolean readBoolean() throws JMSException {
		prepareRead();
		try {
			return Conversions.getBoolean(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		}
	}

	/**
	 * Reads a <code>byte</code> value from the stream message.
	 *
	 * @return the next byte from the stream message as a 8-bit <code>byte</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readByte()
	 */
	public byte readByte() throws JMSException {
		prepareRead();
		try {
			return Conversions.getByte(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());

		} catch (NumberFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a 16-bit integer from the stream message.
	 *
	 * @return a 16-bit integer from the stream message
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readShort()
	 */
	public short readShort() throws JMSException {
		prepareRead();
		try {
			return Conversions.getShort(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		} catch (NumberFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a Unicode character value from the stream message.
	 *
	 * @return a Unicode character from the stream message
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readChar()
	 */
	public char readChar() throws JMSException {
		prepareRead();
		try {
			return Conversions.getChar(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		} catch (NullPointerException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a 32-bit integer from the stream message.
	 *
	 * @return a 32-bit integer value from the stream message, interpreted as
	 *         an <code>int</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readInt()
	 */
	public int readInt() throws JMSException {
		prepareRead();
		try {
			return Conversions.getInt(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		} catch (NumberFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a 64-bit integer from the stream message.
	 *
	 * @return a 64-bit integer value from the stream message, interpreted as a
	 *         <code>long</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readLong()
	 */
	public long readLong() throws JMSException {
		prepareRead();
		try {
			return Conversions.getLong(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		} catch (NumberFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a <code>float</code> from the stream message.
	 *
	 * @return a <code>float</code> value from the stream message
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readFloat()
	 */
	public float readFloat() throws JMSException {
		prepareRead();
		try {
			return Conversions.getFloat(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		} catch (NullPointerException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		} catch (NumberFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a <code>double</code> from the stream message.
	 *
	 * @return a <code>double</code> value from the stream message
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readDouble()
	 */
	public double readDouble() throws JMSException {
		prepareRead();
		try {
			return Conversions.getDouble(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		} catch (NullPointerException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		} catch (NumberFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a <CODE>String</CODE> from the stream message.
	 *
	 * @return a Unicode string from the stream message
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.StreamMessage#readString()
	 */
	public String readString() throws JMSException {
		prepareRead();
		try {
			return Conversions.getString(readNext());
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		}
	}

	/**
	 * Reads a byte array field from the stream message into the specified
	 * <CODE>byte[]</CODE> object (the read buffer).
	 * <p/>
	 * <P>
	 * To read the field value, <CODE>readBytes</CODE> should be successively
	 * called until it returns a value less than the length of the read buffer.
	 * The value of the bytes in the buffer following the last byte read is
	 * undefined.
	 * <p/>
	 * <P>
	 * If <CODE>readBytes</CODE> returns a value equal to the length of the
	 * buffer, a subsequent <CODE>readBytes</CODE> call must be made. If
	 * there are no more bytes to be read, this call returns -1.
	 * <p/>
	 * <P>
	 * If the byte array field value is null, <CODE>readBytes</CODE> returns
	 * -1.
	 * <p/>
	 * <P>
	 * If the byte array field value is empty, <CODE>readBytes</CODE> returns 0.
	 * <p/>
	 * <P>
	 * Once the first <CODE>readBytes</CODE> call on a <CODE>byte[]</CODE>
	 * field value has been made, the full value of the field must be read
	 * before it is valid to read the next field. An attempt to read the next
	 * field before that has been done will throw a <CODE>
	 * MessageFormatException</CODE>.
	 * <p/>
	 * <P>
	 * To read the byte field value into a new <CODE>byte[]</CODE> object,
	 * use the <CODE>readObject</CODE> method.
	 *
	 * @param value the buffer into which the data is read
	 * @return the total number of bytes read into the buffer, or -1 if there
	 *         is no more data because the end of the byte field has been
	 *         reached
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see #readObject()
	 * @see javax.jms.StreamMessage#readBytes(byte[])
	 */
	public int readBytes(byte[] value) throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("StreamMessage write_only");
		}
		getInputStream();
		int read = 0; // the number of bytes read
		if (readBytes == 0) {
			try {
				dis.mark(buffer.length - dis.available());
				byte type = (byte) (dis.readByte() & 0x0F);
				if (type == TYPE_NULL) {
					return -1;
				} else if (type != TYPE_BYTE_ARRAY) {
					dis.reset();
					if (type < TYPE_NAMES.length) {
						throw new MessageFormatException("Expected type="
						        + TYPE_NAMES[TYPE_BYTE_ARRAY]
						        + ", but got type="
						        + TYPE_NAMES[type]);
					} else {
						throw new MessageFormatException("StreamMessage corrupted");
					}
				}
			} catch (IOException e) {
				throw new JMSException(e.getMessage());
			}
			try {
				byteArrayLength = dis.readInt();
			} catch (IOException e) {
				throw new JMSException(e.getMessage());
			}
		}
		if (byteArrayLength == 0) {
			if (readBytes != 0) { // not the first invocation
				read = -1;
			}
			readBytes = 0; // finished reading the byte array
		} else {
			++readBytes;
			try {
				if (value.length <= byteArrayLength) {
					read = value.length;
					dis.readFully(value);
					byteArrayLength -= value.length;
				} else {
					read = byteArrayLength;
					dis.readFully(value, 0, byteArrayLength);
					byteArrayLength = 0;
				}
			} catch (IOException e) {
				throw new JMSException(e.getMessage());
			}
		}
		return read;
	}

	/**
	 * Reads an object from the stream message.
	 * <p/>
	 * <P>
	 * This method can be used to return, in objectified format, an object in
	 * the Java programming language ("Java object") that has been written to
	 * the stream with the equivalent <CODE>writeObject</CODE> method call,
	 * or its equivalent primitive <CODE>write <I>type</I></CODE> method.
	 * <p/>
	 * <P>
	 * Note that byte values are returned as <CODE>byte[]</CODE>, not <CODE>
	 * Byte[]</CODE>.
	 * <p/>
	 * <P>
	 * An attempt to call <CODE>readObject</CODE> to read a byte field value
	 * into a new <CODE>byte[]</CODE> object before the full value of the
	 * byte field has been read will throw a <CODE>MessageFormatException
	 * </CODE>.
	 *
	 * @return a Java object from the stream message, in objectified format
	 *         (for example, if the object was written as an <CODE>int</CODE>,
	 *         an <CODE>Integer</CODE> is returned)
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of message stream has been reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if this type conversion is invalid.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see #readBytes(byte[] value)
	 * @see javax.jms.StreamMessage#readObject()
	 */
	public Object readObject() throws JMSException {
		prepareRead();
		try {
			return readNext();
		} catch (MessageFormatException e) {
			try {
				dis.reset();
			} catch (IOException ex) {
				throw new JMSException(ex.getMessage());
			}
			throw new MessageFormatException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>boolean</code> to the stream message. The value <code>true</code>
	 * is written as the value <code>(byte)1</code>; the value <code>false</code>
	 * is written as the value <code>(byte)0</code>.
	 *
	 * @param value the <code>boolean</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeBoolean(boolean)
	 */
	public void writeBoolean(boolean value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_BOOLEAN);
			dos.writeBoolean(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>byte</code> to the stream message.
	 *
	 * @param value the <code>byte</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeByte(byte)
	 */
	public void writeByte(byte value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_BYTE);
			dos.writeByte((int) value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>short</code> to the stream message.
	 *
	 * @param value the <code>short</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeShort(short)
	 */
	public void writeShort(short value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_SHORT);
			dos.writeShort((int) value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>char</code> to the stream message.
	 *
	 * @param value the <code>char</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeChar(char)
	 */
	public void writeChar(char value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_CHAR);
			dos.writeChar((int) value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes an <code>int</code> to the stream message.
	 *
	 * @param value the <code>int</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeInt(int)
	 */
	public void writeInt(int value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_INT);
			dos.writeInt(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>long</code> to the stream message.
	 *
	 * @param value the <code>long</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeLong(long)
	 */
	public void writeLong(long value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_LONG);
			dos.writeLong(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>float</code> to the stream message.
	 *
	 * @param value the <code>float</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeFloat(float)
	 */
	public void writeFloat(float value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_FLOAT);
			dos.writeFloat(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>double</code> to the stream message.
	 *
	 * @param value the <code>double</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeDouble(double)
	 */
	public void writeDouble(double value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			dos.writeByte((int) TYPE_DOUBLE);
			dos.writeDouble(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>String</code> to the stream message.
	 *
	 * @param value the <code>String</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeString(String)
	 */
	public void writeString(String value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			if (value != null) {
				dos.writeByte((int) TYPE_STRING);
				dos.writeUTF(value);
			} else {
				throw new JMSException("Value is null");
			}
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a byte array field to the stream message.
	 * <p/>
	 * <P>
	 * The byte array <code>value</code> is written to the message as a byte
	 * array field. Consecutively written byte array fields are treated as two
	 * distinct fields when the fields are read.
	 *
	 * @param value the byte array value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeBytes(byte[])
	 */
	public void writeBytes(byte[] value) throws JMSException {
		writeBytes(value, 0, value.length);
	}

	/**
	 * Writes a portion of a byte array as a byte array field to the stream
	 * message.
	 * <p/>
	 * <P>
	 * The a portion of the byte array <code>value</code> is written to the
	 * message as a byte array field. Consecutively written byte array fields
	 * are treated as two distinct fields when the fields are read.
	 *
	 * @param value  the byte array value to be written
	 * @param offset the initial offset within the byte array
	 * @param length the number of bytes to use
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeBytes(byte[], int, int)
	 */
	public void writeBytes(byte[] value, int offset, int length)
	        throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("StreamMessage read_only");
		}

		try {
			if (value != null) {
				dos.writeByte((int) TYPE_BYTE_ARRAY);
				dos.writeInt(length);
				dos.write(value, offset, length);
			} else {
				throw new JMSException("Value is null");
			}
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes an object to the stream message.
	 * <p/>
	 * <P>
	 * This method works only for the objectified primitive object types (
	 * <code>Integer</code>,<code>Double</code>,<code>Long</code>
	 * &nbsp;...), <code>String</code> objects, and byte arrays.
	 *
	 * @param value the Java object to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if the object is invalid.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.StreamMessage#writeObject(Object)
	 */
	public void writeObject(Object value) throws JMSException {
		if (value == null) {
			try {
				if (!isBodyModifiable()) {
					throw new MessageNotWriteableException("StreamMessage read_only");
				}
				getOutputStream();
				dos.writeByte(TYPE_NULL);
			} catch (IOException e) {
				throw new JMSException(e.getMessage());
			}
		} else if (value instanceof Boolean) {
			writeBoolean(((Boolean) value).booleanValue());
		} else if (value instanceof Byte) {
			writeByte(((Byte) value).byteValue());
		} else if (value instanceof byte[]) {
			writeBytes((byte[]) value);
		} else if (value instanceof Short) {
			writeShort(((Short) value).shortValue());
		} else if (value instanceof Character) {
			writeChar(((Character) value).charValue());
		} else if (value instanceof Integer) {
			writeInt(((Integer) value).intValue());
		} else if (value instanceof Long) {
			writeLong(((Long) value).longValue());
		} else if (value instanceof Float) {
			writeFloat(((Float) value).floatValue());
		} else if (value instanceof Double) {
			writeDouble(((Double) value).doubleValue());
		} else if (value instanceof String) {
			writeString((String) value);
		} else {
			throw new MessageFormatException("StreamMessag invalid_type");
		}
	}

	/**
	 * @see javax.jms.StreamMessage#reset()
	 */
	public void reset() throws JMSException {
		try {
			if (!isBodyModifiable()) {
				setBodyModifiable(true);
				if (dos != null) {
					dos.flush();
					buffer = baos.toByteArray();
					baos = null;
					dos.close();
					dos = null;
				}
			} else {
				if (dis != null) {
					bais = null;
					dis.close();
					dis = null;
				}
			}
			readBytes = 0;
		} catch (IOException e) {
			if (e instanceof EOFException) {
				throw new MessageEOFException(e.getMessage());
			} else {
				throw new JMSException(e.getMessage());
			}
		}
	}

	/**
	 * Sets the object reference in the message to null and sets the message to
	 * modifiable.
	 *
	 * @throws javax.jms.JMSException
	 */
	public void clearBody() throws JMSException {
		super.clearBody();
		buffer = new byte[0];
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
		bais = null;
		dis = null;
	}

	/**
	 * Read the next object from the stream and verify that it is one of the
	 * specified data types.
	 *
	 * @return The read object in its original type format
	 * @throws javax.jms.MessageEOFException if the end of the stream is reached.
	 * @throws javax.jms.MessageFormatException
	 *                                       if the type conversion is invalid or the client is
	 *                                       currently reading a bytes field (and hasn't completed).
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is write-only.
	 * @throws javax.jms.JMSException        if an IO error occurs with the underlying message.
	 * @throws NullPointerException          if value is null.
	 */
	private Object readNext() throws JMSException {
		if (readBytes != 0) {
			throw new MessageFormatException("Must finish reading byte array before reading next field");
		}

		byte type = 0;
		try {
			type = dis.readByte();
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
		if ((type & 0x0F) > TYPE_NAMES.length) {
			throw new JMSException("StreamMessage corrupted");
		}
		Object result = null;

		try {
			switch (type & 0x0F) {
				case TYPE_BOOLEAN:
					boolean value = ((type & 0xF0) != 0) ? true : false;
					result = new Boolean(value);
					break;
				case TYPE_BYTE:
					result = new Byte(dis.readByte());
					break;
				case TYPE_BYTE_ARRAY:
					int length = dis.readInt();
					byte[] bytes = new byte[length];
					dis.readFully(bytes);
					result = bytes;
					break;
				case TYPE_SHORT:
					result = new Short(dis.readShort());
					break;
				case TYPE_CHAR:
					result = new Character(dis.readChar());
					break;
				case TYPE_INT:
					result = new Integer(dis.readInt());
					break;
				case TYPE_LONG:
					result = new Long(dis.readLong());
					break;
				case TYPE_FLOAT:
					result = new Float(dis.readFloat());
					break;
				case TYPE_DOUBLE:
					result = new Double(dis.readDouble());
					break;
				case TYPE_STRING:
					result = dis.readUTF();
					break;
			}
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
		return result;
	}

	private void prepareRead() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("StreamMessage write_only");
		}
		getInputStream();
		try {
			dis.mark(buffer.length - dis.available());
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Initialise the input stream if it hasn't been intialised
	 *
	 * @return the input stream
	 */
	private DataInputStream getInputStream() {
		if (dis == null) {
			bais = new ByteArrayInputStream(buffer);
			dis = new DataInputStream(bais);
		}
		return dis;
	}

	/**
	 * Initialise the output stream if it hasn't been intialised
	 *
	 * @return the output stream
	 * @throws java.io.IOException if the output stream can't be created
	 */
	private DataOutputStream getOutputStream() throws IOException {
		if (dos == null) {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			dos.write(buffer);
		}
		return dos;
	}

}
