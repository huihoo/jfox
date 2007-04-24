package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        }
        DateFormat dateFormat = new SimpleDateFormat(dateValidation.format());
        try {
            Date date = dateFormat.parse(inputValue);
            return date;
        }
        catch(ParseException e) {
            throw new ValidateException("input date error, format: " + dateValidation.format(), e);
        }
    }

    public static void main(String[] args) {

    }
}
