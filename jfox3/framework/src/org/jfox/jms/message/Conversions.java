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

import javax.jms.MessageFormatException;

/**
 * <p/>
 * A simple format converter helper class in order to help convert an Objec
 * t type as per the table listed below.
 * </p>
 * <p/>
 * <P>A value written as the row type can be read as the column type.
 * <p/>
 * |        | boolean byte short char int long float double String byte[]
 * |----------------------------------------------------------------------
 * |boolean |    X                                            X
 * |byte    |          X     X         X   X                  X
 * |short   |                X         X   X                  X
 * |char    |                     X                           X
 * |int     |                          X   X                  X
 * |long    |                              X                  X
 * |float   |                                    X     X      X
 * |double  |                                          X      X
 * |String  |    X     X     X         X   X     X     X      X
 * |byte[]  |                                                        X
 * |----------------------------------------------------------------------
 *
 * @author <a href="mailto:founder_chen@yahoo.com.cn">Peter.Cheng</a>
 * @version Revision: 1.1 Date: 2002-12-08 20:22:11
 */
public class Conversions {

	/**
	 * Convert value to boolean
	 *
	 * @param value
	 * @return the converted boolean
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion is invalid
	 */
	public static boolean getBoolean(Object value) throws MessageFormatException {
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
	public static byte getByte(Object value) throws MessageFormatException {
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
	public static short getShort(Object value) throws MessageFormatException {
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
	public static char getChar(Object value) throws MessageFormatException {
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
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
	 */
	public static int getInt(Object value) throws MessageFormatException {
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
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
	 */
	public static long getLong(Object value) throws MessageFormatException {
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
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
	 */
	public static float getFloat(Object value) throws MessageFormatException {
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
	 * @throws NumberFormatException if value is a String and the conversion
	 *                               is invalid
	 */
	public static double getDouble(Object value) throws MessageFormatException {
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
	 * @throws javax.jms.MessageFormatException
	 *          if the conversion is invalid
	 */
	public static String getString(Object value) throws MessageFormatException {
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
	public static byte[] getBytes(Object value) throws MessageFormatException {
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
