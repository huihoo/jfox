/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ActionMethodNotFoundException extends ActionException {

    public ActionMethodNotFoundException(String moduleName, String actionName, String actionMethodName) {
        super("ActionMethod name: " + actionMethodName  + ", Action name: " + actionName + ", Module name: " + moduleName);
    }

    public static void main(String[] args) {

    }
}