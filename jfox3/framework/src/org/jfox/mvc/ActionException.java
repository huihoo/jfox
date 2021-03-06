/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ActionException extends Exception {

    public ActionException(Throwable cause) {
        super(cause);
    }

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void main(String[] args) {

    }
}