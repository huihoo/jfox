/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

import java.lang.annotation.Annotation;

/**
 * String validator
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class FileValidator implements Validator<String> {

    public String validate(String inputValue, Annotation validation) throws ValidateException {
        FileValidation fileValidation = (FileValidation)validation;
        if (inputValue == null || inputValue.trim().length() == 0) {
            if (!fileValidation.nullable()) {
                throw new ValidateException(inputValue, "input can not be null!");
            }
        }
        else {
            if(!inputValue.endsWith(fileValidation.suffix())) {
                throw new ValidateException(inputValue, "the type of the uploaded file should be " + fileValidation.suffix());
            }
        }
        return inputValue;
    }
}