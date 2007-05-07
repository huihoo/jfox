/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class DoubleValidator implements Validator<Double> {

    public Double validate(String inputValue, Annotation validation) throws ValidateException {
        DoubleValidation doubleValidation = (DoubleValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!doubleValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }

        }
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
