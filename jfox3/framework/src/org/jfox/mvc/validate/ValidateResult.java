package code.google.webactioncontainer.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ValidateResult {

    private String inputField;
    private Annotation validateAnnotation;
    private String inputValue;
    
    private boolean success = true;
    private Object returnObject;
    private ValidateException validateException;

    private String errorMessage;

    public ValidateResult(Field inputField, String inputValue, Annotation validateAnnotation) {
        this.inputField = inputField.getName();
        this.inputValue = inputValue;
        this.validateAnnotation = validateAnnotation;
    }

    public ValidateResult(String inputField, String inputValue, Annotation validateAnnotation) {
        this.inputField = inputField;
        this.inputValue = inputValue;
        this.validateAnnotation = validateAnnotation;
    }


    public Object getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInputField() {
        return inputField;
    }

    public String getInputValue() {
        return inputValue;
    }

    public Annotation getValidateAnnotation() {
        return validateAnnotation;
    }

    public ValidateException getValidateException() {
        return validateException;
    }

    public void setValidateException(ValidateException validateException) {
        this.validateException = validateException;
        this.success = false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ValidateError{field=" + inputField + ", input=" + inputValue + "}";
    }
}
