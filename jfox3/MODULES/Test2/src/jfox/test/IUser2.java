/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface IUser2 extends IUser {

    public void setPassword(String password);

    public String getPassword();

}
