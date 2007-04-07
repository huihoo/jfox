package org.jfox.framework.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * 注入一个常量，可以使用 placeholder，placeholder由 global.properties 导入
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Constant {

    /**
     * 对于值注射时，值的对象类型，默认为Field本身的类型
     */
    Class type() default FieldType.class;

    /**
     * 注射一个固定的值，可以带有占位符(如：${database.driver})，会在注入之前使用Velocity解析
     */
    String value() default "";

    /**
     * 由解析器负责根据 Enjection 描述的 Field/Constructor 来判断真实的 type
     */
    public static class FieldType {}

}
