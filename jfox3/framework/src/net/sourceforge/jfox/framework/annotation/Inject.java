package net.sourceforge.jfox.framework.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject {

    /**
     * 由解析器负责根据 Enjection 描述的 Field/Constructor 来判断真实的 type
     */
    Class type() default FieldType.class;

    /**
     * 引用的 Component
     * 默认为 Field 的 class 代表的 Component
     */
    String id() default "";
    
    public static class FieldType {}
}
