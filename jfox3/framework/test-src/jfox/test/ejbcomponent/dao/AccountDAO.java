/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejbcomponent.dao;

import jfox.test.jpa.Account;
import org.jfox.entity.MappedEntity;
import org.jfox.entity.dao.DataAccessObject;

import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface AccountDAO extends DataAccessObject {

    Account getAccountById(long id) throws SQLException;

    Account getAccountByIdSQL(long id) throws SQLException;

    MappedEntity getAccountMappedEntityById(long id) throws SQLException;

    List<Account> getAllAccounts() throws SQLException;

    Account createAccount(String name, String lastname, String mail) throws SQLException;
}
