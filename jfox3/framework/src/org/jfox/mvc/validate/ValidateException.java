/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.validate;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ValidateException extends Exception {

    private String inputField;
    private String inputValue;
    private String errorId;

    // throw by annotation Validator
    public ValidateException(String inputValue, String message) {
        super(message);
        this.inputValue = inputValue;
    }

    // throw by validate all
    public ValidateException(String errorId, String inputFieldName, String inputValue,String message) {
        super(message);
        this.errorId = errorId;
        this.inputValue = inputValue;
        this.inputField = inputFieldName;
    }

    public void setInputField(String inputField) {
        this.inputField = inputField;
    }

    public String getInputField() {
        return inputField;
    }

    public String getInputValue() {
        return inputValue;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getMessage() {
        String message =  super.getMessage();
        String inputInfo = " [name:" + getInputField() + ", value: " + getInputValue() + "]";
        message += inputInfo;
        return message;
    }

    public static void main(String[] args) {

    }
}
