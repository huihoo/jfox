package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Annotation;
import java.util.Date;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class DateValidator implements Validator<Date>{

    public Date validate(String inputValue, Annotation validation) throws ValidateException {
        return null;
    }

    public static void main(String[] args) {

    }
}
