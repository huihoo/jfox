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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

/**
 * <p/>
 * A <CODE>StreamMessage</CODE> object is used to send a stream of primitive
 * types in the Java programming language. It is filled and read sequentially.
 * It inherits from the <CODE>Message</CODE> interface and adds a stream
 * message body. Its methods are based largely on those found in <CODE>
 * java.io.DataInputStream</CODE> and <CODE>java.io.DataOutputStream</CODE>.
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.2 Date: 2002-12-08 19:22:15
 */

public class BytesMessageImpl
        extends JMSMessage
        implements BytesMessage, Serializable {

	private transient DataOutputStream dos = null;
	private transient DataInputStream dis = null;
	private transient ByteArrayOutputStream baos = null;
	private transient ByteArrayInputStream bais = null;
	private byte[] buffer =  new byte[0];

	/**
	 * Default constructor.
	 */
	public BytesMessageImpl() {
		super();
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
	}

	/**
	 * Reads a <code>boolean</code> from the bytes message stream.
	 *
	 * @return the <code>boolean</code> value read
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readBoolean()
	 */
	public boolean readBoolean() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMesage write_only");
		}

		try {
			return getInputStream().readBoolean();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a signed 8-bit value from the bytes message stream.
	 *
	 * @return the next byte from the bytes message stream as a signed 8-bit
	 *         <code>byte</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readByte()
	 */
	public byte readByte() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return this.getInputStream().readByte();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads an unsigned 8-bit number from the bytes message stream.
	 *
	 * @return the next byte from the bytes message stream, interpreted as an
	 *         unsigned 8-bit number
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readUnsignedByte()
	 */
	public int readUnsignedByte() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readUnsignedByte();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessageimpl end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a signed 16-bit number from the bytes message stream.
	 *
	 * @return the next two bytes from the bytes message stream, interpreted as
	 *         a signed 16-bit number
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readShort()
	 */
	public short readShort() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readShort();
		} catch (EOFException e) {
			throw new MessageEOFException("Bytesmessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads an unsigned 16-bit number from the bytes message stream.
	 *
	 * @return the next two bytes from the bytes message stream, interpreted as
	 *         an unsigned 16-bit integer
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readUnsignedShort()
	 */
	public int readUnsignedShort() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readUnsignedShort();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a Unicode character value from the bytes message stream.
	 *
	 * @return the next two bytes from the bytes message stream as a Unicode
	 *         character
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readChar()
	 */
	public char readChar() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readChar();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a signed 32-bit integer from the bytes message stream.
	 *
	 * @return the next four bytes from the bytes message stream, interpreted
	 *         as an <code>int</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readInt()
	 */
	public int readInt() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readInt();
		} catch (EOFException e1) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a signed 64-bit integer from the bytes message stream.
	 *
	 * @return the next eight bytes from the bytes message stream, interpreted
	 *         as a <code>long</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readLong()
	 */
	public long readLong() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return this.getInputStream().readLong();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a <code>float</code> from the bytes message stream.
	 *
	 * @return the next four bytes from the bytes message stream, interpreted
	 *         as a <code>float</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readFloat()
	 */
	public float readFloat() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readFloat();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a <code>double</code> from the bytes message stream.
	 *
	 * @return the next eight bytes from the bytes message stream, interpreted
	 *         as a <code>double</code>
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readDouble()
	 */
	public double readDouble() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readDouble();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a string that has been encoded using a modified UTF-8 format from
	 * the bytes message stream.
	 * <p/>
	 * <P>
	 * For more information on the UTF-8 format, see "File System Safe UCS
	 * Transformation Format (FSS_UTF)", X/Open Preliminary Specification,
	 * X/Open Company Ltd., Document Number: P316. This information also
	 * appears in ISO/IEC 10646, Annex P.
	 *
	 * @return a Unicode string from the bytes message stream
	 * @throws javax.jms.JMSException        if the JMS provider fails to read the message due to some
	 *                                       internal error.
	 * @throws javax.jms.MessageEOFException if unexpected end of bytes stream has been reached.
	 * @throws javax.jms.MessageNotReadableException
	 *                                       if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readUTF()
	 */
	public String readUTF() throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return getInputStream().readUTF();
		} catch (EOFException e) {
			throw new MessageEOFException("BytesMessage end_of_message");
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a byte array from the bytes message stream.
	 * <p/>
	 * <P>
	 * If the length of array <code>value</code> is less than the number of
	 * bytes remaining to be read from the stream, the array should be filled.
	 * A subsequent call reads the next increment, and so on.
	 * <p/>
	 * <P>
	 * If the number of bytes remaining in the stream is less than the length
	 * of array <code>value</code>, the bytes should be read into the array.
	 * The return value of the total number of bytes read will be less than the
	 * length of the array, indicating that there are no more bytes left to be
	 * read from the stream. The next read of the stream returns -1.
	 *
	 * @param value the buffer into which the data is read
	 * @return the total number of bytes read into the buffer, or -1 if there
	 *         is no more data because the end of the stream has been reached
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotReadableException
	 *                                if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readBytes(byte[])
	 */
	public int readBytes(byte[] value) throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		try {
			return this.getInputStream().read(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Reads a portion of the bytes message stream.
	 * <p/>
	 * <P>
	 * If the length of array <code>value</code> is less than the number of
	 * bytes remaining to be read from the stream, the array should be filled.
	 * A subsequent call reads the next increment, and so on.
	 * <p/>
	 * <P>
	 * If the number of bytes remaining in the stream is less than the length
	 * of array <code>value</code>, the bytes should be read into the array.
	 * The return value of the total number of bytes read will be less than the
	 * length of the array, indicating that there are no more bytes left to be
	 * read from the stream. The next read of the stream returns -1.
	 * <p/>
	 * <p/>
	 * If <code>length</code> is negative, or <code>length</code> is
	 * greater than the length of the array <code>value</code>, then an
	 * <code>IndexOutOfBoundsException</code> is thrown. No bytes will be
	 * read from the stream for this exception case.
	 *
	 * @param value  the buffer into which the data is read
	 * @param length the number of bytes to read; must be less than or equal to
	 *               <code>value.length</code>
	 * @return the total number of bytes read into the buffer, or -1 if there
	 *         is no more data because the end of the stream has been reached
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageNotReadableException
	 *                                if the message is in write-only mode.
	 * @see javax.jms.BytesMessage#readBytes(byte[], int)
	 */
	public int readBytes(byte[] value, int length) throws JMSException {
		if (isBodyModifiable()) {
			throw new MessageNotReadableException("BytesMessage write_only");
		}

		if ((length < 0) || (length > value.length)) {
			throw new IndexOutOfBoundsException();
		}

		try {
			return getInputStream().read(value, 0, length);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>boolean</code> to the bytes message stream as a 1-byte
	 * value. The value <code>true</code> is written as the value <code>(byte)1</code>;
	 * the value <code>false</code> is written as the value <code>(byte)0</code>.
	 *
	 * @param value the <code>boolean</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeBoolean(boolean)
	 */
	public void writeBoolean(boolean value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeBoolean(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>byte</code> to the bytes message stream as a 1-byte
	 * value.
	 *
	 * @param value the <code>byte</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeByte(byte)
	 */
	public void writeByte(byte value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeByte((int) value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>short</code> to the bytes message stream as two bytes,
	 * high byte first.
	 *
	 * @param value the <code>short</code> to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeShort(short)
	 */
	public void writeShort(short value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeShort((int) value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>char</code> to the bytes message stream as a 2-byte
	 * value, high byte first.
	 *
	 * @param value the <code>char</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeChar(char)
	 */
	public void writeChar(char value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			this.getOutputStream().writeChar((int) value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes an <code>int</code> to the bytes message stream as four bytes,
	 * high byte first.
	 *
	 * @param value the <code>int</code> to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeInt(int)
	 */
	public void writeInt(int value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeInt(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a <code>long</code> to the bytes message stream as eight bytes,
	 * high byte first.
	 *
	 * @param value the <code>long</code> to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeLong(long)
	 */
	public void writeLong(long value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			this.getOutputStream().writeLong(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Converts the <code>float</code> argument to an <code>int</code>
	 * using the <code>floatToIntBits</code> method in class <code>Float</code>,
	 * and then writes that <code>int</code> value to the bytes message
	 * stream as a 4-byte quantity, high byte first.
	 *
	 * @param value the <code>float</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeFloat(float)
	 */
	public void writeFloat(float value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeFloat(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Converts the <code>double</code> argument to a <code>long</code>
	 * using the <code>doubleToLongBits</code> method in class <code>Double</code>,
	 * and then writes that <code>long</code> value to the bytes message
	 * stream as an 8-byte quantity, high byte first.
	 *
	 * @param value the <code>double</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeDouble(double)
	 */
	public void writeDouble(double value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeDouble(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a string to the bytes message stream using UTF-8 encoding in a
	 * machine-independent manner.
	 * <p/>
	 * <P>
	 * For more information on the UTF-8 format, see "File System Safe UCS
	 * Transformation Format (FSS_UTF)", X/Open Preliminary Specification,
	 * X/Open Company Ltd., Document Number: P316. This information also
	 * appears in ISO/IEC 10646, Annex P.
	 *
	 * @param value the <code>String</code> value to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeUTF(String)
	 */
	public void writeUTF(String value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().writeUTF(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a byte array to the bytes message stream.
	 *
	 * @param value the byte array to be written
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeBytes(byte[])
	 */
	public void writeBytes(byte[] value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			this.getOutputStream().write(value);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes a portion of a byte array to the bytes message stream.
	 *
	 * @param value  the byte array value to be written
	 * @param offset the initial offset within the byte array
	 * @param length the number of bytes to use
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.BytesMessage#writeBytes(byte[], int, int)
	 */
	public void writeBytes(byte[] value, int offset, int length)
	        throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		try {
			getOutputStream().write(value, offset, length);
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Writes an object to the bytes message stream.
	 * <p/>
	 * <P>
	 * This method works only for the objectified primitive object types (
	 * <code>Integer</code>,<code>Double</code>,<code>Long</code>
	 * &nbsp;...), <code>String</code> objects, and byte arrays.
	 *
	 * @param value the object in the Java programming language ("Java object")
	 *              to be written; it must not be null
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if the object is of an invalid type.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @throws NullPointerException   if the parameter <code>value</code> is null.
	 * @see javax.jms.BytesMessage#writeObject(Object)
	 */
	public void writeObject(Object value) throws JMSException {
		if (!isBodyModifiable()) {
			throw new MessageNotWriteableException("BytesMessage read_only");
		}

		if (value instanceof Boolean) {
			writeBoolean(((Boolean) value).booleanValue());
		} else if (value instanceof Byte) {
			writeByte(((Byte) value).byteValue());
		} else if (value instanceof Character) {
			writeChar(((Character) value).charValue());
		} else if (value instanceof Double) {
			writeDouble(((Double) value).doubleValue());
		} else if (value instanceof Float) {
			writeFloat(((Float) value).floatValue());
		} else if (value instanceof Integer) {
			writeInt(((Integer) value).intValue());
		} else if (value instanceof Long) {
			writeLong(((Long) value).longValue());
		} else if (value instanceof Short) {
			writeShort(((Short) value).shortValue());
		} else if (value instanceof String) {
			writeUTF((String) value);
		} else if (value instanceof byte[]) {
			writeBytes((byte[]) value);
		} else {
			throw new MessageFormatException("ByteMessage invalid_type");
		}
	}

	/**
	 * Puts the message body in read-only mode and repositions the stream of
	 * bytes to the beginning.
	 *
	 * @throws javax.jms.JMSException if the JMS provider fails to reset the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if the message has an invalid format.
	 * @see javax.jms.BytesMessage#reset()
	 */
	public void reset() throws JMSException {
		try {
			if (dos != null) {
				dos.flush();
				dis =
				        new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
				setBodyModifiable(false);
			}
		} catch (IOException e) {
			throw new JMSException("Can't reset message " + e.getMessage());
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

	public long getBodyLength() throws JMSException {
		return buffer.length;
	}
}
