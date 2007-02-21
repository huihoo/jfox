package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DoubleValidation {

    /**
     * default double validator class
     */
    Class<? extends Validator> validatorClass() default DoubleValidator.class;


    /**
     * min value
     */
    double minValue() default Double.MIN_VALUE;

    /**
     * max value
     */
    double maxValue() default Double.MAX_VALUE;

    /**
     * nullable
     */
    boolean nullable() default false;
}
