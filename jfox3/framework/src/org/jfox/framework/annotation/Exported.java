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
 * Exported用来标注一个Component 接口，
 * 标注了的Component接口将能够被所有的模块发现，
 * 所以如果一个Component要跨模块提供服务，则需要将服务接口使用 @Exported 描述
 *
 * 描述了 Exported 的接口在 Module reload 的时候，也不会重新加载
 * Exported 描述在Class上，没有意义
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Exported {

}
