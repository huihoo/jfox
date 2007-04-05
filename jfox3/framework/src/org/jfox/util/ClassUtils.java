package org.jfox.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public class ClassUtils {

    public static final  Class[] EMPTY_CLASS_ARRAY = new Class[0];

    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    private static Map<String, Class> primitiveStringToClass = new HashMap<String, Class>();

    static {
        primitiveStringToClass.put(boolean.class.getName(), Boolean.class);
        primitiveStringToClass.put(byte.class.getName(), Byte.class);
        primitiveStringToClass.put(char.class.getName(), Character.class);
        primitiveStringToClass.put(short.class.getName(), Short.class);
        primitiveStringToClass.put(int.class.getName(), Integer.class);
        primitiveStringToClass.put(long.class.getName(), Long.class);
        primitiveStringToClass.put(double.class.getName(), Double.class);
        primitiveStringToClass.put(float.class.getName(), Float.class);
    }

    private ClassUtils() {
    }

    public static Class[] getAllSuperclasses(Class cls) {
        if (cls == null) {
            return new Class[0];
        }
        List<Class> classList = new ArrayList<Class>();
        Class superClass = cls;
        while (superClass != null) { // java.lang.Object 也算为超类
            classList.add(superClass);
            superClass = superClass.getSuperclass();
        }
        return classList.toArray(new Class[classList.size()]);
    }

    public static Class[] getAllInterfaces(Class clazz) {
        if (clazz == null) {
            return new Class[0];
        }
        List<Class> classList = new ArrayList<Class>();
        while (clazz != null) {
            Class[] interfaces = clazz.getInterfaces();
            for (Class interf : interfaces) {
                if (!classList.contains(interf)) {
                    classList.add(interf);
                }
                Class[] superInterfaces = getAllInterfaces(interf);
                for (Class superIntf : superInterfaces ) {
                    if (!classList.contains(superIntf)) {
                        classList.add(superIntf);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return classList.toArray(new Class[classList.size()]);
    }

    public static boolean isAssignable(Class clazz, Class toClass) {
        if (clazz == null || toClass == null) {
            return false;
        }
        if (clazz.equals(toClass)) {
            return true;
        }

        Class newClazz = clazz;
        Class newToClass = toClass;
        if(clazz.isPrimitive()) {
            newClazz = primitiveStringToClass.get(clazz.getName());
        }
        if(toClass.isPrimitive()){
            newToClass = primitiveStringToClass.get(toClass.getName());
        }
        return newToClass.isAssignableFrom(newClazz);
    }

    public static boolean isAssignable(Class[] classArray, Class[] toClassArray) {
        if (classArray == null || toClassArray == null) {
            return false;
        }
        if (classArray.length != toClassArray.length) {
            return false;
        }

        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * if is primitive class, get it's concrete class object else use reflect
     * with a string paramter constructor
     *
     * @param clazz class
     * @param value value
     * @return Object
     * @throws NoSuchMethodException e
     * @throws InstantiationException e
     * @throws IllegalAccessException e
     * @throws java.lang.reflect.InvocationTargetException e
     */
    public static <T> T newObject(Class<T> clazz, String value)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Object obj;
        if (clazz.isArray()) { // 数组
            Class<?> elementClass = clazz.getComponentType();
            StringTokenizer st = new StringTokenizer(value);
            int length = st.countTokens();
            Object array = Array.newInstance(elementClass, length);
            for (int i = 0; st.hasMoreTokens(); i++) {
                Object param = newObject(elementClass, st.nextToken());
                Array.set(array, i, param);
            }
            obj = array;
        }
        else {
            Class<T> clz = clazz;
            if (clazz.isPrimitive()) {
                clz = primitiveStringToClass.get(clazz.getName());
            }
            if(clz.equals(String.class)){
                obj = value;
            }
            else {
                // 使用 String 构造器
                obj = clz.getConstructor(String.class).newInstance(value);
            }
        }
        return (T)obj;
    }

    public static boolean isInnerClass(Class cls) {
        return (cls == null) ? false : (cls.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) >= 0);
    }


    public static Class getClass(String className) throws ClassNotFoundException {
        Class clazz;
        if (isPrimitiveClass(className)) {
            return primitiveStringToClass.get(className);
        }
        else {
            clazz = Class.forName(className);
        }
        return clazz;
    }


    public static Class getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        Class clazz;
        if (isPrimitiveClass(className)) {
            return primitiveStringToClass.get(className);
        }
        else {
            clazz = classLoader.loadClass(className);
        }
        return clazz;
    }

    /**
     * 可以处理传入的参数为定义参数的子类的情况
     *
     * @param clazz class
     * @param methodName method name
     * @param expectedTypes parameter types
     * @throws NoSuchMethodException if not found such method
     */
    public static Method getCompatibleMethod(Class clazz, String methodName, Class[] expectedTypes) throws NoSuchMethodException {
        Method theMethod = null;
        try {
            theMethod = clazz.getMethod(methodName, expectedTypes);
        }
        catch (NoSuchMethodException e) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    if (isAssignable(expectedTypes, method.getParameterTypes())) {
                        if (theMethod == null) {
                            theMethod = method;
                        }
                        else {
                            // 找到最适合的方法
                            if (isAssignable(method.getParameterTypes(), theMethod.getParameterTypes())) {
                                theMethod = method;
                            }
                        }
                    }
                }
            }
            if (theMethod == null)
                throw e;
        }
        return theMethod;
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class[] expectedTypes) throws NoSuchMethodException {
        Constructor<T> constructor = null;
        if (expectedTypes == null) {
            expectedTypes = EMPTY_CLASS_ARRAY;
        }
        try {
            constructor = clazz.getConstructor(expectedTypes);
        }
        catch (NoSuchMethodException e) {
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor constr : constructors) {
                if (isAssignable(expectedTypes, constr.getParameterTypes())) {
                    if (constructor == null) {
                        constructor = (Constructor<T>)constr;
                    }
                    else {
                        if (isAssignable(constr.getParameterTypes(), constructor.getParameterTypes())) {
                            constructor = (Constructor<T>)constr;
                        }
                    }
                }
            }
            if (constructor == null)
                throw e;
        }
        return constructor;
    }

    /**
     * 取得该类申明的Field，包括在其超类中申明
     *
     * @param clazz class
     * @param name field name
     * @throws NoSuchFieldException if not found such field
     */
    public static Field getDecaredField(Class clazz, String name) throws NoSuchFieldException {
        Field field = null;
        Class[] superClasses = getAllSuperclasses(clazz);
        for(Class superClass : superClasses){
            try {
                field = superClass.getDeclaredField(name);
                break;
            }
            catch (NoSuchFieldException e) {
                // ignore
            }
        }
        if (field == null) {
            throw new NoSuchFieldException("No such declared field " + name + " in " + clazz);
        }
        return field;
    }

    /**
     * 取得clz 所有的 Field
     *
     * @param clazz class
     */
    public static Field[] getAllDecaredFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();
//        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class[] superClasses = getAllSuperclasses(clazz);
        for(Class superClass : superClasses){
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * get the narrowest class from the given types
     *
     * @param types class array
     */
    public static Class getNarrowestClass(Class[] types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("types is null or empty.");
        }
        Class clz = types[0];
        for (int i = 1; i < types.length; i++) {
            if (clz.isAssignableFrom(types[i])) {
                clz = types[i];
            }
            else if (!types[i].isAssignableFrom(clz)) {
                throw new IllegalArgumentException(types[i].getName() + " not has assignable relation with " + clz.getName());
            }
        }
        
        return clz;
    }

    public static boolean isPrimitiveClass(String primitiveClassName) {
        return primitiveStringToClass.containsKey(primitiveClassName);
    }

    public static boolean isPrimitiveClass(Class primitiveClass) {
        return primitiveStringToClass.containsKey(primitiveClass.getName());
    }

    public static boolean isPrimitiveWrapperClass(Class primitiveWrapperClass){
        return primitiveStringToClass.values().contains(primitiveWrapperClass);
    }

    public static void main(String[] args) throws Exception {
        String[] strArray = newObject((new String[0]).getClass(), "1 2 3");
        System.out.println(Arrays.toString(strArray));
        System.out.println(newObject(boolean.class,"true"));
        System.out.println(Arrays.toString(getAllSuperclasses(ClassUtils.class)));
    }

}
