/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字符串验证 Annotation
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StringValidation {

    /**
     * default String validator class 
     */
    Class<? extends Validator> validatorClass() default StringValidator.class;

    /**
     * 最小长度
     */
    int minLength() default Integer.MIN_VALUE;

    /**
     * 最大长度
     */
    int maxLength() default Integer.MAX_VALUE;

    /**
     * 是否可以为空
     */
    boolean nullable() default false;

    //TODO: 检查正则表达式
    String regexp() default "";
}
