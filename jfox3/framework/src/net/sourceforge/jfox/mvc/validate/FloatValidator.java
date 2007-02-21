package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
public class FloatValidator implements Validator<Float>{

    public Float validate(String inputValue, Annotation validation) throws ValidateException {
        FloatValidation intv = (FloatValidation) validation;
        return Float.valueOf(inputValue);
    }

    public static void main(String[] args) {

    }
}
