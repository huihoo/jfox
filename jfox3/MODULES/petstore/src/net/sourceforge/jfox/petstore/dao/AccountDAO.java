package net.sourceforge.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import net.sourceforge.jfox.petstore.entity.Account;

public interface AccountDAO {

    Account getAccount(String username) throws SQLException;

    Account getAccount(String username, String password) throws SQLException;

    void insertAccount(Account account) throws SQLException;

    void updateAccount(Account account) throws SQLException;

    List<String> getUsernameList() throws SQLException;

}
