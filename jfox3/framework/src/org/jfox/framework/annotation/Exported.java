package org.jfox.framework.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Export Annotation 用来标注一个Component 接口
 * 标注了的Component接口将被Export至 ClassLoaderRepository
 * Exported 接口在 Module reload 的时候，也不会重新加载
 *
 * exposed描述在Class上，没有意义
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Exported {

}
