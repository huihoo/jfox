/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述在 Component 上以便将该 Component 发布至IoC容器中
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ComponentBean {
    /**
     * 该组件实现的接口，组件只能通过指定的接口来访问，
     * 如果没有指定接口呢，则为所有接口
     */
    Class[] interfaces() default {};

    /**
     * 部署的 id
     * Default 为Component实现类的类名的简称
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
     * 优先级，在一个模块中组件，优先级值小的会被加载
     */
    int priority() default 50;

    /**
     * 描述信息
     */
    String description() default "";


}
