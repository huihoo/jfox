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
public class FloatValidator implements Validator<Float> {

    public Float validate(String inputValue, Annotation validation) throws ValidateException {
        FloatValidation floatValidation = (FloatValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!floatValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }

        }
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
