/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms.message;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;

/**
 * <p/>
 * Description: A <CODE>MapMessage</CODE> object is used to send a set of
 * name-value pairs. The names are <CODE>String</CODE> objects, and the
 * values are primitive data types in the Java programming language. The
 * entries can be accessed sequentially or randomly by name. The order of the
 * entries is undefined. <CODE>MapMessage</CODE> inherits from the <CODE>
 * Message</CODE> interface and adds a message body that contains a Map.
 * </p>
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-12-08 12:50:10
 */

public class MapMessageImpl
        extends JMSMessage
        implements MapMessage, Serializable {

	private HashMap map = new HashMap();

	/**
	 * Default constructor.
	 */
	public MapMessageImpl() {
		super();
	}

	/**
	 * Returns the <CODE>boolean</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>boolean</CODE>
	 * @return the <CODE>boolean</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getBoolean(String)
	 */
	public boolean getBoolean(String name) throws JMSException {
		return getBoolean(map.get(name));
	}

	/**
	 * Returns the <CODE>byte</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>byte</CODE>
	 * @return the <CODE>byte</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getByte(String)
	 */
	public byte getByte(String name) throws JMSException {
		return getByte(map.get(name));
	}

	/**
	 * Returns the <CODE>short</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>short</CODE>
	 * @return the <CODE>short</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getShort(String)
	 */
	public short getShort(String name) throws JMSException {
		return getShort(map.get(name));
	}

	/**
	 * Returns the Unicode character value with the specified name.
	 *
	 * @param name the name of the Unicode character
	 * @return the Unicode character value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getChar(String)
	 */
	public char getChar(String name) throws JMSException {
		return getChar(map.get(name));
	}

	/**
	 * Returns the <CODE>int</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>int</CODE>
	 * @return the <CODE>int</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getInt(String)
	 */
	public int getInt(String name) throws JMSException {
		return getInt(map.get(name));
	}

	/**
	 * Returns the <CODE>long</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>long</CODE>
	 * @return the <CODE>long</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getLong(String)
	 */
	public long getLong(String name) throws JMSException {
		return getLong(map.get(name));
	}

	/**
	 * Returns the <CODE>float</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>float</CODE>
	 * @return the <CODE>float</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getFloat(String)
	 */
	public float getFloat(String name) throws JMSException {
		return getFloat(map.get(name));
	}

	/**
	 * Returns the <CODE>double</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>double</CODE>
	 * @return the <CODE>double</CODE> value with the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getDouble(String)
	 */
	public double getDouble(String name) throws JMSException {
		return getDouble(map.get(name));
	}

	/**
	 * Returns the <CODE>String</CODE> value with the specified name.
	 *
	 * @param name the name of the <CODE>String</CODE>
	 * @return the <CODE>String</CODE> value with the specified name; if
	 *         there is no item by this name, a null value is returned
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getString(String)
	 */
	public String getString(String name) throws JMSException {
		return getString(map.get(name));
	}

	/**
	 * Returns the byte array value with the specified name.
	 *
	 * @param name the name of the byte array
	 * @return a copy of the byte array value with the specified name; if there
	 *         is no item by this name, a null value is returned.
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 * @see javax.jms.MapMessage#getBytes(String)
	 */
	public byte[] getBytes(String name) throws JMSException {
		return getBytes(map.get(name));
	}

	/**
	 * Returns the value of the object with the specified name.
	 * <p/>
	 * <P>
	 * This method can be used to return, in objectified format, an object in
	 * the Java programming language ("Java object") that had been stored in
	 * the Map with the equivalent <CODE>setObject</CODE> method call, or its
	 * equivalent primitive <CODE>set <I>type</I></CODE> method.
	 * <p/>
	 * <P>
	 * Note that byte values are returned as <CODE>byte[]</CODE>, not <CODE>
	 * Byte[]</CODE>.
	 *
	 * @param name the name of the Java object
	 * @return a copy of the Java object value with the specified name, in
	 *         objectified format (for example, if the object was set as an
	 *         <CODE>int</CODE>, an <CODE>Integer</CODE> is returned); if
	 *         there is no item by this name, a null value is returned
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @see javax.jms.MapMessage#getObject(String)
	 */
	public Object getObject(String name) throws JMSException {
		return map.get(name);
	}

	/**
	 * Returns an <CODE>Enumeration</CODE> of all the names in the <CODE>
	 * MapMessage</CODE> object.
	 *
	 * @return an enumeration of all the names in this <CODE>MapMessage
	 *         </CODE>
	 * @throws javax.jms.JMSException if the JMS provider fails to read the message due to some
	 *                                internal error.
	 * @see javax.jms.MapMessage#getMapNames()
	 */
	public Enumeration getMapNames() throws JMSException {
		return Collections.enumeration(map.keySet());
	}

	/**
	 * Sets a <CODE>boolean</CODE> value with the specified name into the
	 * Map.
	 *
	 * @param name  the name of the <CODE>boolean</CODE>
	 * @param value the <CODE>boolean</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setBoolean(String, boolean)
	 */
	public void setBoolean(String name, boolean value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Boolean(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMesage read_only");
		}
	}

	/**
	 * Sets a <CODE>byte</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>byte</CODE>
	 * @param value the <CODE>byte</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setByte(String, byte)
	 */
	public void setByte(String name, byte value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Byte(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a <CODE>short</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>short</CODE>
	 * @param value the <CODE>short</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setShort(String, short)
	 */
	public void setShort(String name, short value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Short(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a Unicode character value with the specified name into the Map.
	 *
	 * @param name  the name of the Unicode character
	 * @param value the Unicode character value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setChar(String, char)
	 */
	public void setChar(String name, char value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Character(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets an <CODE>int</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>int</CODE>
	 * @param value the <CODE>int</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setInt(String, int)
	 */
	public void setInt(String name, int value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Integer(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a <CODE>long</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>long</CODE>
	 * @param value the <CODE>long</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setLong(String, long)
	 */
	public void setLong(String name, long value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Long(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a <CODE>float</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>float</CODE>
	 * @param value the <CODE>float</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setFloat(String, float)
	 */
	public void setFloat(String name, float value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Float(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a <CODE>double</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>double</CODE>
	 * @param value the <CODE>double</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setDouble(String, double)
	 */
	public void setDouble(String name, double value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, new Double(value));
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a <CODE>String</CODE> value with the specified name into the Map.
	 *
	 * @param name  the name of the <CODE>String</CODE>
	 * @param value the <CODE>String</CODE> value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setString(String, String)
	 */
	public void setString(String name, String value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				map.put(name, value);
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Sets a byte array value with the specified name into the Map.
	 *
	 * @param name  the name of the byte array
	 * @param value the byte array value to set in the Map; the array is copied
	 *              so that the value for <CODE>name</CODE> will not be altered
	 *              by future modifications
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setBytes(String, byte[])
	 */
	public void setBytes(String name, byte[] value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				int len = value.length;
				byte[] valueCopy = new byte[len];
				System.arraycopy(value, 0, valueCopy, 0, len);
				map.put(name, valueCopy);
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMmessage read_only");
		}

	}

	/**
	 * Sets a portion of the byte array value with the specified name into the
	 * Map.
	 *
	 * @param name   the name of the byte array
	 * @param value  the byte array value to set in the Map
	 * @param offset the initial offset within the byte array
	 * @param length the number of bytes to use
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setBytes(String, byte[], int, int)
	 */
	public void setBytes(String name, byte[] value, int offset, int length)
	        throws JMSException {
		if (isBodyModifiable()) {
			try {
				byte[] newValue = new byte[length];
				System.arraycopy(value, offset, newValue, 0, length);
				map.put(name, newValue);
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessageimpl read_only");
		}
	}

	/**
	 * Sets an object value with the specified name into the Map.
	 * <p/>
	 * <P>
	 * This method works only for the objectified primitive object types (
	 * <code>Integer</code>,<code>Double</code>,<code>Long</code>
	 * &nbsp;...), <code>String</code> objects, and byte arrays.
	 *
	 * @param name  the name of the Java object
	 * @param value the Java object value to set in the Map
	 * @throws javax.jms.JMSException if the JMS provider fails to write the message due to
	 *                                some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if the object is invalid.
	 * @throws javax.jms.MessageNotWriteableException
	 *                                if the message is in read-only mode.
	 * @see javax.jms.MapMessage#setObject(String, Object)
	 */
	public void setObject(String name, Object value) throws JMSException {
		if (isBodyModifiable()) {
			try {
				if ((value instanceof Boolean)
				        || (value instanceof Byte)
				        || (value instanceof Short)
				        || (value instanceof Character)
				        || (value instanceof Integer)
				        || (value instanceof Long)
				        || (value instanceof Float)
				        || (value instanceof Double)
				        || (value instanceof String)
				        || (value instanceof byte[])) {
					map.put(name, value);
				} else {
					throw new MessageFormatException("MapMessagei invalid_type");
				}
			} catch (NullPointerException e) {
				throw new JMSException(e.getMessage());
			}
		} else {
			throw new MessageNotWriteableException("MapMessage read_only");
		}
	}

	/**
	 * Indicates whether an item exists in this <CODE>MapMessage</CODE>
	 * object.
	 *
	 * @param name the name of the item to test
	 * @return true if the item exists
	 * @throws javax.jms.JMSException if the JMS provider fails to determine if the item exists
	 *                                due to some internal error.
	 * @see javax.jms.MapMessage#itemExists(String)
	 */
	public boolean itemExists(String name) throws JMSException {
		return map.containsKey(name);
	}

	/**
	 * Clear out the message body.
	 *
	 * @throws javax.jms.JMSException
	 */
	public void clearBody() throws JMSException {
		super.clearBody();
		map = new HashMap();
	}

	/**
	 * Convert value to boolean
	 *
	 * @param value
	 * @return the converted boolean
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion is invalid
	 */
	private boolean getBoolean(Object value) throws MessageFormatException {
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		} else if (value instanceof String) {
			return Boolean.valueOf((String) value).booleanValue();
		} else if (value == null) {
			return Boolean.valueOf((String) value).booleanValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Boolean");
		}
	}

	/**
	 * Convert value to byte
	 *
	 * @param value
	 * @return the converted byte
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion is invalid
	 */
	private byte getByte(Object value) throws MessageFormatException {
		if (value instanceof Byte) {
			return ((Byte) value).byteValue();
		} else if (value instanceof String) {
			return Byte.parseByte((String) value);
		} else if (value == null) {
			return Byte.valueOf((String) value).byteValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Byte");
		}
	}

	/**
	 * Convert value to short
	 *
	 * @param value
	 * @return the converted short
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion is invalid
	 */
	private short getShort(Object value) throws MessageFormatException {
		if (value instanceof Short) {
			return ((Short) value).shortValue();
		} else if (value instanceof Byte) {
			return ((Byte) value).shortValue();
		} else if (value instanceof String) {
			return Short.parseShort((String) value);
		} else if (value == null) {
			return Short.valueOf((String) value).shortValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Short");
		}
	}

	/**
	 * Convert value to char
	 *
	 * @param value the object to convert from
	 * @return the converted char
	 * @throws javax.jms.MessageFormatException
	 *                              if the conversion is invalid
	 * @throws NullPointerException if value is null
	 */
	private char getChar(Object value) throws MessageFormatException {
		if (value == null) {
			return ((Character) null).charValue();
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Char");
		}
	}

	/**
	 * Convert value to int
	 *
	 * @param value
	 * @return the converted int
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion is invalid
	 */
	private int getInt(Object value) throws MessageFormatException {
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		} else if (value instanceof Byte) {
			return ((Byte) value).intValue();
		} else if (value instanceof Short) {
			return ((Short) value).intValue();
		} else if (value instanceof String) {
			return Integer.parseInt((String) value);
		} else if (value == null) {
			return Integer.valueOf((String) value).intValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Int");
		}
	}

	/**
	 * Convert value to long
	 *
	 * @param value
	 * @return the converted long
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion is invalid
	 */
	private long getLong(Object value) throws MessageFormatException {
		if (value instanceof Long) {
			return ((Long) value).longValue();
		} else if (value instanceof Byte) {
			return ((Byte) value).longValue();
		} else if (value instanceof Short) {
			return ((Short) value).longValue();
		} else if (value instanceof Integer) {
			return ((Integer) value).longValue();
		} else if (value instanceof String) {
			return Long.parseLong((String) value);
		} else if (value == null) {
			return Long.valueOf((String) value).longValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Long");
		}
	}

	/**
	 * Convert value to float
	 *
	 * @param value
	 * @return the converted float
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion is invalid
	 */
	private float getFloat(Object value) throws MessageFormatException {
		if (value instanceof Float) {
			return ((Float) value).floatValue();
		} else if (value instanceof String) {
			return Float.parseFloat((String) value);
		} else if (value == null) {
			return Float.valueOf((String) value).floatValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Float");
		}
	}

	/**
	 * Convert value to double
	 *
	 * @param value the object to convert from
	 * @return the converted double
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion is invalid
	 */
	private double getDouble(Object value) throws MessageFormatException {
		if (value instanceof Double) {
			return ((Double) value).doubleValue();
		} else if (value instanceof Float) {
			return ((Float) value).doubleValue();
		} else if (value instanceof String) {
			return Double.parseDouble((String) value);
		} else if (value == null) {
			return Double.valueOf((String) value).doubleValue();
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to Double");
		}
	}

	/**
	 * Convert value to String
	 *
	 * @param value the object to convert from
	 * @return the converted String
	 *          if the conversion is invalid
	 */
	private String getString(Object value) {
		return (value == null) ? null : String.valueOf(value);
	}

	/**
	 * Convert value to Bytes
	 *
	 * @param value the object to convert from
	 * @return the converted bytes
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion invalid
	 */
	private byte[] getBytes(Object value) throws MessageFormatException {
		if (value == null) {
			return (byte[]) value;
		} else if (value instanceof byte[]) {
			return (byte[]) value;
		} else {
			throw new MessageFormatException("Can't convert value of type "
			        + value.getClass().getName()
			        + " to byte[].");
		}
	}
}
