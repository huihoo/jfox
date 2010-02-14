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
import java.util.Date;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class DateValidator implements Validator<Date>{

    public Date validate(String inputValue, Annotation validation) throws ValidateException {
        DateValidation dateValidation = (DateValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!dateValidation.nullable()) {
                throw new ValidateException("input can not be null!", inputValue);
            }
            else {
                return new Date();
            }
        }
        DateFormat dateFormat = new SimpleDateFormat(dateValidation.format());
        try {
            Date date = dateFormat.parse(inputValue);
            return date;
        }
        catch(ParseException e) {
            throw new ValidateException(inputValue, "input date error, format: " + dateValidation.format());
        }
    }

    public static void main(String[] args) {

    }
}
