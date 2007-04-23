package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class DoubleValidator implements Validator<Double> {

    public Double validate(String inputValue, Annotation validation) throws ValidateException {
        DoubleValidation intv = (DoubleValidation)validation;
        try {
            return Double.valueOf(inputValue);
        }
        catch (NumberFormatException e) {
            throw new ValidateException("Illegal double format for input: " + inputValue, inputValue);
        }
    }

    public static void main(String[] args) {

    }
}
