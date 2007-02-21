package jfox.test;

import jfox.test.IUser;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface IUser2 extends IUser {

    public void setPassword(String password);

    public String getPassword();

}
