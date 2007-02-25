package net.sourceforge.jfox.entity.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MappedColumn {
    /**
     * 定义的 query 的名字
     */
    String namedQuery();

    /**
     * 传给 namedQuery 的参数，要引用改Entity，使用$this，比如 $this.getId()
     */
    ParameterMap[] params() default {};
}
