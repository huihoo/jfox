package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class LongValidator implements Validator<Long> {

    public Long validate(String inputValue, Annotation validation) throws ValidateException {
        LongValidation longValidation = (LongValidation)validation;
        // 整型数据
        long minValue =  longValidation.minValue();
        long maxValue =  longValidation.maxValue();
        try {
            long intValue = Long.parseLong(inputValue);
            if (intValue < minValue || intValue > maxValue) {
                throw new ValidateException("The input value " + inputValue + " must between " + minValue + ", " + maxValue, inputValue);
            }
            return intValue;
        }
        catch (NumberFormatException e) {
            throw new ValidateException("Illegal Integer format for input: " + inputValue, inputValue);
        }
    }

    public static void main(String[] args) {

    }
}
