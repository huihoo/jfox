/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class StringDateValidator implements Validator<String>{

    public String validate(String inputValue, Annotation validation) throws ValidateException {
        DateValidation dateValidation = (DateValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!dateValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }
            else {
                return "";
            }
        }
        DateFormat dateFormat = new SimpleDateFormat(dateValidation.format());
        try {
            dateFormat.parse(inputValue);
            return inputValue;
        }
        catch(ParseException e) {
            throw new ValidateException("input date error, format: " + dateValidation.format(), e);
        }
    }

    public static void main(String[] args) {

    }
}