package net.sourceforge.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import net.sourceforge.jfox.entity.EntityManagerExt;
import net.sourceforge.jfox.entity.EntityObject;
import net.sourceforge.jfox.framework.example.Account;
import net.sourceforge.jfox.framework.example.AccountDAOImpl;
import net.sourceforge.jfox.framework.example.AccountDAO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class DAOTest {
    
    static AccountDAO accountDAO = null;

    @BeforeClass
    public static void setup() throws Exception {
        
        final EntityManager em = Persistence.createEntityManagerFactory("DefaultMysqlDS").createEntityManager();

        accountDAO = new AccountDAOImpl(){
            public EntityManager getEntityManager() {
                return (EntityManagerExt)em;
            }
        };
    }

    @Test
    public void testGetAccount() throws Exception {
        Account account = accountDAO.getAccountById(1L);
        System.out.println("Account: " + account + ", Helper toString: " + ((Account.Helper)((EntityObject)account).helper()).toString(account));
        System.out.println("Account Address: " + account.getAddress());
        Assert.assertEquals(account.getId(),1L);
    }

    @Test
    public void testGetAccountBySQL() throws Exception {
        Account account = accountDAO.getAccountByIdSQL(1L);
        System.out.println("Account: " + account + ", Helper toString: " + ((Account.Helper)((EntityObject)account).helper()).toString(account));
        System.out.println("Account Address: " + account.getAddress());
        Assert.assertEquals(account.getId(),1L);
    }

    @Test
    public void testGetAccountBySQLResultEntityObject() throws Exception {
        EntityObject account = accountDAO.getAccountByIdSQLEntityObject(1L);
        System.out.println("Account: " + account);
        Assert.assertEquals(account.getColumnValue("ACC_ID"),"1");
    }


    @Test
    public void testGetAllAccounts() throws Exception {
        List<Account> accounts = accountDAO.getAllAccounts();
        for(Account account : accounts){
            System.out.println(account);
        }
    }

    @Test
    public void testCreateAccount() throws Exception {
        Account account = accountDAO.createAccount("Yang","Yong","jfox.young@gmail.com");
        Assert.assertEquals(account.getFirstName(), "Yang");
    }

    
    public static void main(String[] args) {

    }
}
