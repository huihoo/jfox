package code.google.webactioncontainer.dao;

import code.google.jcontainer.annotation.Resolve;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Resolve(resolverClass = DAOAnnotationResolver.class, when= Resolve.WHEN.AFTER_CONSTRUCT)
public @interface DAO {
    String name() default "";
}
