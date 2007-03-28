package org.jfox.mvc.validate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntegerValidation {
    /**
     * default integer validator class
     */
    Class<? extends Validator> validatorClass() default IntegerValidator.class;

    /**
     * min value
     */
    int minValue() default Integer.MIN_VALUE;

    /**
     * max value
     */
    int maxValue() default Integer.MAX_VALUE;

    /**
     * nullable 
     */
    boolean nullable() default false;
}
