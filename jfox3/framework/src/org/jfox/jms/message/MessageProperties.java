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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;

/**
 * <p/>
 * Built-in facility for supporting property values.
 * </p>
 * <p/>
 * <p/>
 * <p/>
 * <P>Message properties support the following conversion table. The marked
 * cases must be supported. The unmarked cases must throw a
 * <CODE>JMSException</CODE>. The <CODE>String</CODE>-to-primitive conversions
 * may throw a runtime exception if the
 * primitive's <CODE>valueOf</CODE> method does not accept the
 * <CODE>String</CODE> as a valid representation of the primitive.
 * <p/>
 * <P>A value written as the row type can be read as the column type.
 * <p/>
 * <PRE>
 * |        | boolean byte short int long float double String
 * |----------------------------------------------------------
 * |boolean |    X                                       X
 * |byte    |          X     X    X   X                  X
 * |short   |                X    X   X                  X
 * |int     |                     X   X                  X
 * |long    |                         X                  X
 * |float   |                               X     X      X
 * |double  |                                     X      X
 * |String  |    X     X     X    X   X     X     X      X
 * |----------------------------------------------------------
 * </PRE>
 * <p/>
 * <P>In addition to the type-specific set/get methods for properties, JMS
 * provides the <CODE>setObjectProperty</CODE> and
 * <CODE>getObjectProperty</CODE> methods. These support the same set of
 * property types using the objectified primitive values. Their purpose is
 * to allow the decision of property type to made at execution time rather
 * than at compile time. They support the same property value conversions.
 * <p/>
 * <P>The <CODE>setObjectProperty</CODE> method accepts values of class
 * <CODE>Boolean</CODE>, <CODE>Byte</CODE>, <CODE>Short</CODE>,
 * <CODE>Integer</CODE>, <CODE>Long</CODE>, <CODE>Float</CODE>,
 * <CODE>Double</CODE>, and <CODE>String</CODE>. An attempt
 * to use any other class must throw a <CODE>JMSException</CODE>.
 * <p/>
 * <P>The <CODE>getObjectProperty</CODE> method only returns values of class
 * <CODE>Boolean</CODE>, <CODE>Byte</CODE>, <CODE>Short</CODE>,
 * <CODE>Integer</CODE>, <CODE>Long</CODE>, <CODE>Float</CODE>,
 * <CODE>Double</CODE>, and <CODE>String</CODE>.
 * <p/>
 * <P>The order of property values is not defined. To iterate through a
 * message's property values, use <CODE>getPropertyNames</CODE> to retrieve
 * a property name enumeration and then use the various property get methods
 * to retrieve their values.
 * <p/>
 * <P>A message's properties are deleted by the <CODE>clearProperties</CODE>
 * method. This leaves the message with an empty set of properties.
 * <p/>
 * <P>Getting a property value for a name which has not been set returns a
 * null value. Only the <CODE>getStringProperty</CODE> and
 * <CODE>getObjectProperty</CODE> methods can return a null value.
 * Attempting to read a null value as a primitive type must be treated as
 * calling the primitive's corresponding <CODE>valueOf(String)</CODE>
 * conversion method with a null value.
 * <p/>
 * <P>The JMS API reserves the <CODE>JMSX</CODE> property name prefix for JMS
 * defined properties.
 * The full set of these properties is defined in the Java Message Service
 * specification. New JMS defined properties may be added in later versions
 * of the JMS API.  Support for these properties is optional. The
 * <CODE>String[] ConnectionMetaData.getJMSXPropertyNames</CODE> method
 * returns the names of the JMSX properties supported by a connection.
 * <p/>
 * <P>JMSX properties may be referenced in message selectors whether or not
 * they are supported by a connection. If they are not present in a
 * message, they are treated like any other absent property.
 * <p/>
 * <P>JMSX properties defined in the specification as "set by provider on
 * send" are available to both the producer and the consumers of the message.
 * JMSX properties defined in the specification as "set by provider on
 * receive" are available only to the consumers.
 * <p/>
 * <P><CODE>JMSXGroupID</CODE> and <CODE>JMSXGroupSeq</CODE> are standard
 * properties that clients
 * should use if they want to group messages. All providers must support them.
 * Unless specifically noted, the values and semantics of the JMSX properties
 * are undefined.
 * <p/>
 * <P>The JMS API reserves the <CODE>JMS_<I>vendor_name</I></CODE> property
 * name prefix for provider-specific properties. Each provider defines its own
 * value for <CODE><I>vendor_name</I></CODE>. This is the mechanism a JMS
 * provider uses to make its special per-message services available to a JMS
 * client.
 * <p/>
 * <P>The purpose of provider-specific properties is to provide special
 * features needed to integrate JMS clients with provider-native clients in a
 * single JMS application. They should not be used for messaging between JMS
 * clients.
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-12-01 21:57:33
 */

public class MessageProperties implements Serializable {

	// JMS-Standard Tags and Values
	private static final String JMSX_USERID = "JMSXUserID";
	private static final String JMSX_APPID = "JMSXAppID";
	private static final String JMSX_GROUPID = "JMSXGroupID";
	private static final String JMSX_GROUPSEQ = "JMSXGroupSeq";

	private Hashtable properties = new Hashtable();


	/**
	 * Sets a <CODE>boolean</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>boolean</CODE> property
	 * @param value the <CODE>boolean</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setBooleanProperty(String name, boolean value) throws JMSException {
		setProperty(name, new Boolean(value));
	}

	/**
	 * Returns the value of the <CODE>boolean</CODE> property with the
	 * specified name.
	 *
	 * @param name the name of the <CODE>boolean</CODE> property
	 * @return the <CODE>boolean</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public boolean getBooleanProperty(String name) throws JMSException {
		return getBoolean(properties.get(name));
	}

	/**
	 * Sets a <CODE>byte</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>byte</CODE> property
	 * @param value the <CODE>byte</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setByteProperty(String name, byte value) throws JMSException {
		setProperty(name, new Byte(value));
	}

	/**
	 * Returns the value of the <CODE>byte</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>byte</CODE> property
	 * @return the <CODE>byte</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public byte getByteProperty(String name) throws JMSException {
		return getByte(properties.get(name));
	}

	/**
	 * Sets a <CODE>short</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>short</CODE> property
	 * @param value the <CODE>short</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setShortProperty(String name, short value) throws JMSException {
		setProperty(name, new Short(value));
	}

	/**
	 * Returns the value of the <CODE>short</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>short</CODE> property
	 * @return the <CODE>short</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public short getShortProperty(String name) throws JMSException {
		return getShort(properties.get(name));
	}

	/**
	 * Sets an <CODE>int</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>int</CODE> property
	 * @param value the <CODE>int</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setIntProperty(String name, int value) throws JMSException {
		setProperty(name, new Integer(value));
	}

	/**
	 * Returns the value of the <CODE>int</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>int</CODE> property
	 * @return the <CODE>int</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public int getIntProperty(String name) throws JMSException {
		return getInt(properties.get(name));
	}

	/**
	 * Sets a <CODE>long</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>long</CODE> property
	 * @param value the <CODE>long</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setLongProperty(String name, long value) throws JMSException {
		setProperty(name, new Long(value));
	}

	/**
	 * Returns the value of the <CODE>long</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>long</CODE> property
	 * @return the <CODE>long</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public long getLongProperty(String name) throws JMSException {
		return getLong(properties.get(name));
	}

	/**
	 * Sets a <CODE>float</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>float</CODE> property
	 * @param value the <CODE>float</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setFloatProperty(String name, float value) throws JMSException {
		setProperty(name, new Float(value));
	}

	/**
	 * Returns the value of the <CODE>float</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>float</CODE> property
	 * @return the <CODE>float</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public float getFloatProperty(String name) throws JMSException {
		return getFloat(properties.get(name));
	}

	/**
	 * Sets a <CODE>double</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>double</CODE> property
	 * @param value the <CODE>double</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setDoubleProperty(String name, double value) throws JMSException {
		setProperty(name, new Double(value));
	}

	/**
	 * Returns the value of the <CODE>double</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>double</CODE> property
	 * @return the <CODE>double</CODE> property value for the specified name
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public double getDoubleProperty(String name) throws JMSException {
		return getDouble(properties.get(name));
	}

	/**
	 * Sets a <CODE>String</CODE> property value with the specified name into
	 * the message.
	 *
	 * @param name  the name of the <CODE>String</CODE> property
	 * @param value the <CODE>String</CODE> property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	public void setStringProperty(String name, String value) throws JMSException {
		setProperty(name, new String(value));
	}

	/**
	 * Returns the value of the <CODE>String</CODE> property with the specified
	 * name.
	 *
	 * @param name the name of the <CODE>String</CODE> property
	 * @return the <CODE>String</CODE> property value for the specified name;
	 *         if there is no property by this name, a null value is returned
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if this type conversion is invalid.
	 */
	public String getStringProperty(String name) throws JMSException {
		return getString(properties.get(name));
	}

	/**
	 * Sets a Java object property value with the specified name into the
	 * message.
	 * <p/>
	 * <P>Note that this method works only for the objectified primitive
	 * object types (<CODE>Integer</CODE>, <CODE>Double</CODE>,
	 * <CODE>Long</CODE> ...) and <CODE>String</CODE> objects.
	 *
	 * @param name  the name of the Java object property
	 * @param value the Java object property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 * @throws javax.jms.MessageFormatException
	 *                                if the object is invalid
	 */
	public void setObjectProperty(String name, Object value) throws JMSException {
		if (value instanceof Boolean) {
			setBooleanProperty(name, ((Boolean) value).booleanValue());
		} else if (value instanceof Byte) {
			setByteProperty(name, ((Byte) value).byteValue());
		} else if (value instanceof Short) {
			setShortProperty(name, ((Short) value).shortValue());
		} else if (value instanceof Integer) {
			setIntProperty(name, ((Integer) value).intValue());
		} else if (value instanceof Long) {
			setLongProperty(name, ((Long) value).longValue());
		} else if (value instanceof Float) {
			setFloatProperty(name, ((Float) value).floatValue());
		} else if (value instanceof Double) {
			setDoubleProperty(name, ((Double) value).doubleValue());
		} else if (value instanceof String) {
			setStringProperty(name, (String) value);
		} else if (value == null) {
			setProperty(name, null);
		} else {
			throw new MessageFormatException("Does not support objects of " + "type=" + value.getClass().getName());
		}
	}

	/**
	 * Returns the value of the Java object property with the specified name.
	 * <p/>
	 * <P>This method can be used to return, in objectified format,
	 * an object that has been stored as a property in the message with the
	 * equivalent <CODE>setObjectProperty</CODE> method call, or its equivalent
	 * primitive <CODE>set<I>type</I>Property</CODE> method.
	 *
	 * @param name the name of the Java object property
	 * @return the Java object property value with the specified name, in
	 *         objectified format (for example, if the property was set as an
	 *         <CODE>int</CODE>, an <CODE>Integer</CODE> is
	 *         returned); if there is no property by this name, a null value
	 *         is returned
	 * @throws javax.jms.JMSException if the JMS provider fails to get the property
	 *                                value due to some internal error.
	 */
	public Object getObjectProperty(String name) throws JMSException {
		Object value = properties.get(name);
		if (value != null) {
			if (value instanceof Boolean) {
				return new Boolean(((Boolean) value).booleanValue());
			} else if (value instanceof Byte) {
				return new Byte(((Byte) value).byteValue());
			} else if (value instanceof Short) {
				return new Short(((Short) value).shortValue());
			} else if (value instanceof Integer) {
				return new Integer(((Integer) value).intValue());
			} else if (value instanceof Long) {
				return new Long(((Long) value).longValue());
			} else if (value instanceof Float) {
				return new Float(((Float) value).floatValue());
			} else if (value instanceof Double) {
				return new Double(((Double) value).doubleValue());
			} else {
				return new String((String) value);
			}
		} else {
			return null;
		}
	}


	/**
	 * Inner common method setProperty
	 *
	 * @param name  the name of the Java object property
	 * @param value the Java object property value to set
	 * @throws javax.jms.JMSException if the JMS provider fails to set the property
	 *                                due to some internal error.
	 */
	private void setProperty(String name, Object value) throws JMSException {
		if (name == null) {
			throw new JMSException("It's invalid property name");
		} else {
			if (name.equalsIgnoreCase("NULL") ||
			        name.equalsIgnoreCase("TRUE") ||
			        name.equalsIgnoreCase("FALSE") ||
			        name.equals("NOT") ||
			        name.equals("AND") ||
			        name.equals("OR") ||
			        name.equals("BETWEEN") ||
			        name.equals("LIKE") ||
			        name.equals("IN") ||
			        name.equals("IS")) {
				throw new JMSException("Invalid property name");
			} else {
				if (name.startsWith("JMS") && !name.startsWith("JMS_")) {
					throw new JMSException("Properties cannot begin with JMS");
				} else {
					properties.put(name, value);
				}
			}
		}
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
			        + value.getClass().getName() + " to Boolean");
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
			        + value.getClass().getName() + " to Byte");
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
			        + value.getClass().getName() + " to Short");
		}
	}

	/**
	 * Convert value to int
	 *
	 * @param value
	 * @return the converted int
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
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
			        + value.getClass().getName() + " to Int");
		}
	}


	/**
	 * Convert value to long
	 *
	 * @param value
	 * @return the converted long
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
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
			        + value.getClass().getName() + " to Long");
		}
	}

	/**
	 * Convert value to float
	 *
	 * @param value
	 * @return the converted float
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
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
			        + value.getClass().getName() + " to Float");
		}
	}

	/**
	 * Convert value to double
	 *
	 * @param value the object to convert from
	 * @return the converted double
	 * @throws javax.jms.MessageFormatException
	 *                               if the conversion is invalid
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
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
			        + value.getClass().getName() + " to Double");
		}
	}

	/**
	 * Convert value to String
	 *
	 * @param value the object to convert from
	 * @return the converted String
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion is invalid
	 */
	private String getString(Object value) throws MessageFormatException {
		return (value == null) ? null : String.valueOf(value);
	}


	/**
	 * Clear any values contained in the properties section of the message.
	 *
	 * @see javax.jms.Message
	 */
	public void clearProperties() {
		properties.clear();
	}

	/**
	 * Determine if the specified property exists.
	 *
	 * @see javax.jms.Message
	 */
	public boolean propertyExists(String name) {
		return properties.containsKey(name);
	}

	/**
	 * Returns an <CODE>Enumeration</CODE> of all the property names.
	 *
	 * @return an enumeration of all the names of property values
	 */
	public Enumeration getPropertyNames() {
		return Collections.enumeration(properties.keySet());
	}
}
