/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc.validate;

import java.lang.annotation.Annotation;

/**
 * Validator interface
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
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
