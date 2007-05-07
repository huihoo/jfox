/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * 用来标注一个组件的Field，以使得该组件在实例化的时候，由IoC容器进行依赖注入
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject {

    /**
     * 注入的组件的类型
     * 如果未指定，则由IoC容器根据Filed的类型判断
     */
    Class type() default FieldType.class;

    /**
     * 通过 component id进行注入
     */
    String id() default "";
    
    public static class FieldType {}
}
