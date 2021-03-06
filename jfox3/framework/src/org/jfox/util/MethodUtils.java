/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MethodUtils {
    private static MessageDigest md5 = null;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Map<Long, Method> objectMethods = new HashMap<Long, Method>();

    static {
        try {
            Method[] methods = Object.class.getDeclaredMethods();
            for(Method m : methods){
                objectMethods.put(getMethodHash(m), m);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 是否是定义在 Object 中的方法
     * @param method 方法
     */
    public static boolean isObjectMethod(Method method){
        return objectMethods.containsKey(getMethodHash(method));
    }

    /**
     * get the exclusive method hash code
     *
     * @param method method
     */
    public static long getMethodHash(Method method) {
        long hash = 0;
        Class[] parameterTypes = method.getParameterTypes();
        String methodDesc = method.getName() + "(";
        for (Class type :  parameterTypes) {
            methodDesc += getTypeString(type);
        }
        methodDesc += ")" + getTypeString(method.getReturnType());

        try {
            byte[] bytes = md5.digest(methodDesc.getBytes("ISO-8859-1"));
            for (int j = 0; j < Math.min(8, bytes.length); j++) {
                hash += (long)(bytes[j] & 0xff) << j * 8;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    private static String getTypeString(Class cl) {
        if (cl == Byte.TYPE) {
            return "B";
        }
        else if (cl == Character.TYPE) {
            return "C";
        }
        else if (cl == Double.TYPE) {
            return "D";
        }
        else if (cl == Float.TYPE) {
            return "F";
        }
        else if (cl == Integer.TYPE) {
            return "I";
        }
        else if (cl == Long.TYPE) {
            return "J";
        }
        else if (cl == Short.TYPE) {
            return "S";
        }
        else if (cl == Boolean.TYPE) {
            return "Z";
        }
        else if (cl == Void.TYPE) {
            return "V";
        }
        else if (cl.isArray()) {
            return "[" + getTypeString(cl.getComponentType());
        }
        else {
            return "L" + cl.getName().replace('.', '/') + ";";
        }
    }

    /**
     * is defined in this clazz or in it's super interface or super class
     *
     * @param method method
     * @param clazz class
     */
    public static boolean isDeclaredBy(Method method, Class clazz) {
        Class declaredClass = method.getDeclaringClass();
        if (declaredClass == clazz) {
            return true;
        }
        else {
            // defined in super interface or super class
            if (clazz.isAssignableFrom(declaredClass)) {
                try {
                    clazz.getMethod(method.getName(), method.getParameterTypes());
                    return true;
                }
                catch (NoSuchMethodException e) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

    }

    public static boolean isGetMethod(Method method) {
        if (((method.getName().startsWith("get") && !method.getName().equals("get"))
                || (method.getName().startsWith("is") && !method.getName().equals("is")))
                && method.getReturnType() != void.class
                && method.getParameterTypes().length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isSetMethod(Method method) {
        if (method.getName().startsWith("set")
                && !method.getName().equals("set")
                && method.getReturnType() == void.class
                && method.getParameterTypes().length == 1) {
            return true;
        }
        return false;
    }

    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isPrivate(Method method) {
        return Modifier.isPrivate(method.getModifiers());
    }

    public static boolean isAbstract(Method method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    public static void main(String[] args) {
        MethodUtils.getTypeString(Object.class);
    }
}
