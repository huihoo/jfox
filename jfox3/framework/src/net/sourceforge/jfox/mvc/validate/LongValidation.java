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
public @interface LongValidation {
    /**
     * default integer validator class
     */
    Class<? extends Validator> validatorClass() default LongValidator.class;

    /**
     * min value
     */
    long minValue() default Long.MIN_VALUE;

    /**
     * max value
     */
    long maxValue() default Long.MAX_VALUE;

    /**
     * nullable
     */
    boolean nullable() default false;
}
