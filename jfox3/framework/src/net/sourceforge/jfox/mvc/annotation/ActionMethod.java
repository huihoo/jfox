package net.sourceforge.jfox.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.jfox.mvc.Invocation;

/**
 * 表示一个 Action 方法
 * 用该 Annotation 描述的方法，需要满足以下条件
 * 1.只有一个参数 Invotation
 * 2.返回 PageContext 类型
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ActionMethod {

    public static enum TargetMethod {
        FORWARD, REDIRECT
    }

    /**
     * 调用成功时，跳转的页面 
     */
    String successView();

    /**
     * 发生错误时，跳转的页面
     */
    String errorView() default "";

    /**
     * 跳转的方式，默认为 forward
     */
    TargetMethod targetMethod() default TargetMethod.FORWARD;

    /**
     * 用来装载Http Request参数的类，name 为 input name
     *
     * 支持 file upload，文件上传的类类型必须为 FileUploaded
     */
    Class invocationClass() default Invocation.class;
}
