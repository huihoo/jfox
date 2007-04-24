package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class EmailValidator implements Validator<String> {

    public String validate(String inputValue, Annotation validation) throws ValidateException {
        EmailValidation emailValidation = (EmailValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!emailValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }
            else {
                return "";
            }

        }
        int atIndex = inputValue.indexOf(inputValue);
        int dotIndex = inputValue.lastIndexOf(".");
        if(atIndex < 0 || atIndex <0 || atIndex > dotIndex){
            throw new ValidateException("Illegal email input: " + inputValue, inputValue);
        }
        return inputValue;
    }

    public static void main(String[] args) {

    }
}
