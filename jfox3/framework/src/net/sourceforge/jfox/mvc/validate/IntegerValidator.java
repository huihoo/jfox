package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class IntegerValidator implements Validator<Integer> {

    public Integer validate(String inputValue, Annotation validation) throws ValidateException {
        IntegerValidation integerValidation = (IntegerValidation)validation;
        // 整型数据
        int minValue = integerValidation.minValue();
        int maxValue = integerValidation.maxValue();
        try {
            int intValue = Integer.parseInt(inputValue);
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
