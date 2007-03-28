package org.jfox.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public class AnnotationUtils {

    /**
     * method 是否被类 annotation 注解
     *
     * @param method     method
     * @param annotation annotation class
     */
    public static boolean isAnnotated(Method method, Class<? extends Annotation> annotation) {
        return method.isAnnotationPresent(annotation);
    }

    public static Constructor[] getAnnotatedConstructors(Class clazz, Class<? extends Annotation> annotation) {
        List<Constructor> constructorList = new ArrayList<Constructor>();
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.isAnnotationPresent(annotation)) {
                constructorList.add(constructor);
            }
        }
        return constructorList.toArray(new Constructor[constructorList.size()]);
    }

    public static Field[] getAnnotatedFields(Class clazz, Class<? extends Annotation> annotation) {
        List<Field> fieldList = new ArrayList<Field>();
        Field[] fields = ClassUtils.getAllDecaredFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotation)) {
                fieldList.add(field);
            }
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }


    public static Method[] getAnnotatedMethods(Class clz, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<Method>();
        Method[] allMethods = clz.getMethods();
        for (int i = 0; i < allMethods.length; i++) {
            if (isAnnotated(allMethods[i], annotation)) {
                methods.add(allMethods[i]);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static Method[] getAnnotatedDeclaredMethods(Class clz, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<Method>();
        Method[] allMethods = clz.getDeclaredMethods();
        for (int i = 0; i < allMethods.length; i++) {
            if (isAnnotated(allMethods[i], annotation)) {
                methods.add(allMethods[i]);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static Method[] getAnnotatedSetMethods(Class clz, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<Method>();
        Method[] allMethods = clz.getMethods();
        for (int i = 0; i < allMethods.length; i++) {
            if (MethodUtils.isSetMethod(allMethods[i])
                    && isAnnotated(allMethods[i], annotation)) {
                methods.add(allMethods[i]);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static Method[] getAnnotatedGetMethods(Class clz, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<Method>();
        Method[] allMethods = clz.getMethods();
        for (int i = 0; i < allMethods.length; i++) {
            if (MethodUtils.isGetMethod(allMethods[i])
                    && isAnnotated(allMethods[i], annotation)) {
                methods.add(allMethods[i]);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static boolean hasAnnotatedMethod(Class clz, Class<? extends Annotation> annotation) {
        Method[] allMethods = clz.getMethods();
        for (int i = 0; i < allMethods.length; i++) {
            if (isAnnotated(allMethods[i], annotation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnnotated(Field field, Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    public static void main(String[] args) {

    }
}
