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
public @interface FileValidation {

    /**
     * validate error msgId, defined in validate_msg.properties
     */
     String errorId();

    /**
     * default String validator class 
     */
     Class<? extends Validator> validatorClass() default FileValidator.class;

    /**
     * 是否可以为空
     */
     boolean nullable() default false;
    
    String suffix() default "*";
}