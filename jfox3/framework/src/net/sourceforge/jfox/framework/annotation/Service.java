package net.sourceforge.jfox.framework.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * 描述在 Component 上以便将该 Component 作为 Service 发布
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Service {
    /**
     * 该组件实现的接口，这些接口将被 export
     * 如果没有通过 annotation 指定接口呢，则为所有接口
     */
    Class[] interfaces() default {};

    /**
     * 部署的 id
     * Default 为Component实现类的类名
     */
    String id() default "";

    /**
     * 是否在 getInstance 时，才实例化
     * 否则，在部署之后，立即进行实例化
     */
    boolean active() default false;

    /**
     * 是否单例，单例表示该组件只能部署一次
     */
    boolean singleton() default true;

    /**
     * 描述信息
     */
    String description() default "";

    /**
     * 优先级
     */
    int priority() default 50;
    
}
