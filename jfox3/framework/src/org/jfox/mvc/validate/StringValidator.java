/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.Annotation;

/**
 * String validator
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class StringValidator implements Validator<String> {

    public String validate(String inputValue, Annotation validation) throws ValidateException {
        StringValidation stringValidation = (StringValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!stringValidation.nullable()) {
                throw new ValidateException(inputValue, "input can not be null!");
            }
        }
        else {
            if(inputValue.length() > stringValidation.maxLength() || inputValue.length() < stringValidation.minLength()) {
                throw new ValidateException(inputValue, "input length must between [" + stringValidation.minLength() + "," + stringValidation.maxLength() + "] !");
            }
        }
        return inputValue;
    }
}
