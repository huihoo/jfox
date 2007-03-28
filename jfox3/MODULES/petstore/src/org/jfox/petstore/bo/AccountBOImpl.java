package org.jfox.petstore.bo;

import java.util.List;
import java.util.Collections;
import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.jfox.petstore.entity.Account;
import org.jfox.petstore.dao.AccountDAO;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless
@Local
public class AccountBOImpl implements AccountBO {

    @EJB
    AccountDAO accountDAO;

    public Account getAccount(String username) {
        try {
            return accountDAO.getAccount(username);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Account getAccount(String username, String password) {
        try {
            return accountDAO.getAccount(username, password);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void updateAccount(Account account) {
        try {
            accountDAO.updateAccount(account);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }


    public List<String> getUsernameList() {
        try {
            return accountDAO.getUsernameList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void insertAccount(Account account) {
        try {
            accountDAO.insertAccount(account);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public static void main(String[] args) {

    }
}
