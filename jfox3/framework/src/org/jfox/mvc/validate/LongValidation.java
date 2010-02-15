/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LongValidation {

    String errorId();
    
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
