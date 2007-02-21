package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
public class StringValidator implements Validator<String> {

    public String validate(String inputValue, Annotation validation) throws ValidateException {
        StringValidation stringValidation = (StringValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!stringValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }

        }
        else {
            if(inputValue.length() > stringValidation.maxLength() || inputValue.length() < stringValidation.minLength()) {
                throw new ValidateException("input length must between [" + stringValidation.minLength() + "," + stringValidation.maxLength() + "] !", inputValue);
            }
        }
        return inputValue;
    }

    public static void main(String[] args) {

    }
}
