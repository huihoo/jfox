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
public class ActionMethodNotFoundRuntimeException extends ActionRuntimeException {

    public ActionMethodNotFoundRuntimeException(String actionName, String actionMethodName, String httpMethod) {
        super("WebAction name: " + actionName + ", action method name: " + actionMethodName + ", http method: " + httpMethod);
    }

}