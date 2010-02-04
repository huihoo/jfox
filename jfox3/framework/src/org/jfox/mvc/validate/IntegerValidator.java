/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.Annotation;

/**
 * validate integer input
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class IntegerValidator implements Validator<Integer> {

    public Integer validate(String inputValue, Annotation validation) throws ValidateException {
        IntegerValidation integerValidation = (IntegerValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!integerValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }
        }
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

}
