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
public @interface DoubleValidation {

    String errorId();
    
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
