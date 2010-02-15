/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import code.google.webactioncontainer.ActionResubmitException;
import code.google.webactioncontainer.ParameterObject;
import code.google.webactioncontainer.velocity.VelocityUtils;
import org.apache.log4j.Logger;
import code.google.webactioncontainer.InvocationException;


/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@SuppressWarnings("unchecked")
public class Validators {
    private static Logger logger = Logger.getLogger(Validators.class);

    private static final String VALIDATE_ERROR_MESSAGE_PROPERTIES = "validate_msg.properties";

    /**
     * validator annotation class => validatorClass() method
     */
    private final static Map<Class<? extends Annotation>, Method> validatorMethodMap = new HashMap<Class<? extends Annotation>, Method>();

    /**
     * Validator class => Validator instance
     */
    private final static Map<Class<? extends Validator>, Validator> validatorMap = new HashMap<Class<? extends Validator>, Validator>();

    private static Properties validateErrorMessages = new Properties();

    static {
        try {
            validateErrorMessages.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(VALIDATE_ERROR_MESSAGE_PROPERTIES));
        }
        catch (Exception e) {
            logger.warn("Failed to load validate error message properties: " + VALIDATE_ERROR_MESSAGE_PROPERTIES);
        }
    }

    public static ValidateResult validate(Field field, String input, Annotation validationAnnotation) {
        ValidateResult validateResult = new ValidateResult(field, input, validationAnnotation);
        Validator validator = getValidator(validationAnnotation);
        if(validator == null) {
            validateResult.setSuccess(true);
            validateResult.setReturnObject(input);
        }
        try {
            Object returnObject = validator.validate(input, validationAnnotation);
            validateResult.setSuccess(true);
            validateResult.setReturnObject(returnObject);
        }
        catch (ValidateException ve) {
            // validate失败，设置异常，由最后ActionSupport抛出异常，这样更好处理
            validateResult.setValidateException(ve);
            validateResult.setErrorMessage(getErrorMessage(field, input, validationAnnotation));
        }
        return validateResult;
    }

    /**
     * 根据 Validation Annotation 获得 Validator
     * @param validation
     * @return
     */
    synchronized static Validator getValidator(Annotation validation) {

        if (!validatorMethodMap.containsKey(validation.getClass())) {
            try {
                Method validatorClassMethod = validation.getClass().getMethod("validatorClass");
                validatorMethodMap.put(validation.getClass(), validatorClassMethod);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to reflect method Class<? extends Validator> validatorClass() from validator: " + validation, e);
            }
        }
        Method validatorClassMethod = validatorMethodMap.get(validation.getClass());

        Class<? extends Validator> validatorClass = null;
        try {
            validatorClass = (Class<? extends Validator>)validatorClassMethod.invoke(validation);
        }
        catch(Exception e) {
            throw new RuntimeException("Failed to invoke  validatorClass() method from validator: " + validation, e);
        }
        
        if (!validatorMap.containsKey(validatorClass)) {
            try {
                Validator validator = validatorClass.newInstance();
                validatorMap.put(validatorClass, validator);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed Instantiate validator, " + validatorClass.getName(), e);
            }
        }
        return validatorMap.get(validatorClass);
    }

    public static boolean isValidationAnnotation(Annotation validationAnnotation) {
        try {
            validationAnnotation.getClass().getMethod("validatorClass");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ValidateResult validateNullable(ParameterObject parameterObject, Field field, Annotation validationAnnotation){
        ValidateResult validateResult = new ValidateResult(field, null, validationAnnotation);
        try {
            field.setAccessible(true);
            Object value = field.get(parameterObject);
            Method method = validationAnnotation.getClass().getMethod("nullable");
            Boolean isNullable = (Boolean)method.invoke(validationAnnotation);
            if(value == null && !isNullable){
                validateResult.setSuccess(false);
                validateResult.setErrorMessage(getErrorMessage(field, "", validationAnnotation));
            }
            return validateResult;
        }
        catch (IllegalAccessException e) {
            throw new ActionResubmitException("Validate nullable field.", e);
        }
        catch (Exception e) {
            e.printStackTrace();
            // no nullable method in annotation
            validateResult.setSuccess(true);
            return validateResult;
        }
    }

    public static String getErrorId(Annotation validationAnnotation){
        try {
            Method method = validationAnnotation.getClass().getMethod("errorId");
            return (String)method.invoke(validationAnnotation);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getErrorMessage(Field field, String inputValue, Annotation validationAnnotation) {
        String errorId = getErrorId(validationAnnotation);
        String inputField = field.getName();
        return getErrorMessage(inputField, inputValue, errorId);
    }

    public static String getErrorMessage(String inputField, String inputValue, String errorId) {
        if(inputValue == null) {
            inputValue = "";
        }
        if(!validateErrorMessages.containsKey(errorId)) {
            throw new ValidateErrorIdNotFoundRuntimeException("errorId=" + errorId);
        }
        String errorMessage = validateErrorMessages.getProperty(errorId);
        Map<String, Object> velocityContext = new HashMap<String, Object>();
        velocityContext.put(inputField, inputValue);
        errorMessage = VelocityUtils.evaluate(errorMessage, velocityContext);
        return errorMessage;
    }


    public static void main(String[] args) {
        System.out.println(validateErrorMessages.get("1"));
    }
}
