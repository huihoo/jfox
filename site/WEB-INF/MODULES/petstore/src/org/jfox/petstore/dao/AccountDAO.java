/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import org.jfox.petstore.entity.Account;

public interface AccountDAO {

    Account getAccount(String username) throws SQLException;

    Account getAccount(String username, String password) throws SQLException;

    void insertAccount(Account account) throws SQLException;

    void updateAccount(Account account) throws SQLException;

    List<String> getUsernameList() throws SQLException;

}
