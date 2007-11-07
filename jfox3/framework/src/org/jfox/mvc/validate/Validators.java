/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfox.mvc.InvocationException;


/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@SuppressWarnings("unchecked")
public class Validators {

    /**
     * validator annotation class => validatorClass() method
     */
    private static Map<Class<? extends Annotation>, Method> validatorMethodMap = new HashMap<Class<? extends Annotation>, Method>();

    /**
     * Validator class => Validator instance
     */
    private static Map<Class<? extends Validator>, Validator> validatorMap = new HashMap<Class<? extends Validator>, Validator>();

    static Logger logger = Logger.getLogger(Validators.class); 

    public static Object validate(Field field, String input, Annotation validation) throws ValidateException, InvocationException {
        Validator validator = getValidator(validation);
        if(validator == null) {
            return input;
        }
        try {
            return validator.validate(input, validation);
        }
        catch (ValidateException ve) {
            ve.setInputField(field.getName());
            throw ve;
        }
    }

    /**
     * 根据 Validation Annotation 获得 Validator
     * @param validation
     * @return
     * @throws InvocationException
     */
    synchronized static Validator getValidator(Annotation validation) throws InvocationException {

        if (!validatorMethodMap.containsKey(validation.getClass())) {
            try {
                Method validatorClassMethod = validation.getClass().getMethod("validatorClass");
                validatorMethodMap.put(validation.getClass(), validatorClassMethod);
            }
            catch (Exception e) {
                logger.warn("Failed to reflect method Class<? extends Validator> validatorClass() from validator: " + validation, e);
                return null;
            }
        }
        Method validatorClassMethod = validatorMethodMap.get(validation.getClass());

        Class<? extends Validator> validatorClass = null;
        try {
            validatorClass = (Class<? extends Validator>)validatorClassMethod.invoke(validation);
        }
        catch(Exception e) {
            throw new InvocationException("Failed to invoke  validatorClass() method from validator: " + validation, e);
        }
        
        if (!validatorMap.containsKey(validatorClass)) {
            try {
                Validator validator = validatorClass.newInstance();
                validatorMap.put(validatorClass, validator);
            }
            catch (Exception e) {
                throw new InvocationException("Failed Instantiate validator, " + validatorClass.getName(), e);
            }
        }
        return validatorMap.get(validatorClass);
    }

    public static boolean isValidationAnnotation(Annotation annotation) {
        try {
            annotation.getClass().getMethod("validatorClass");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {

    }
}
