/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.bo;

import java.util.List;

import org.jfox.petstore.entity.Account;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface AccountBO {
    Account getAccount(String username);

    Account getAccount(String username, String password);

    void updateAccount(Account account);

    void insertAccount(Account account);

    List<String> getUsernameList();
}
