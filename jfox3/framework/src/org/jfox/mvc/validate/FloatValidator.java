package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class FloatValidator implements Validator<Float> {

    public Float validate(String inputValue, Annotation validation) throws ValidateException {
        FloatValidation intv = (FloatValidation)validation;
        try {
            return Float.valueOf(inputValue);
        }
        catch (NumberFormatException e) {
            throw new ValidateException("Illegal float format for input: " + inputValue, inputValue);
        }

    }

    public static void main(String[] args) {

    }
}
