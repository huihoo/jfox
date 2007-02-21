package net.sourceforge.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
public interface Validator<T> {

    /**
     *  verify the input according validatation
     *  return Object constructed by input if success
     * @param inputValue input string
     * @param validation validation annotation
     * @throws ValidateException if validate failed
     */
    public T validate(String inputValue, Annotation validation) throws ValidateException;
}
