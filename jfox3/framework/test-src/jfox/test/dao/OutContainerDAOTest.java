/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.dao;

import jfox.test.ejbcomponent.dao.AccountDAO;
import jfox.test.ejbcomponent.dao.AccountDAOImpl;
import jfox.test.jpa.Account;
import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.MappedEntity;
import org.jfox.entity.dao.DAOSupport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class OutContainerDAOTest {

    static AccountDAO accountDAO = null;

    @BeforeClass
    public static void setup() throws Exception {

        final EntityManager em = Persistence.createEntityManagerFactory("default").createEntityManager();

        accountDAO = new AccountDAOImpl(){
            public EntityManager getEntityManager() {
                return (EntityManagerExt)em;
            }
        };
    }

    @Test
    public void testGetAccount() throws Exception {
        Account account = accountDAO.getAccountById(1L);
        System.out.println("Account Address: " + account.getAddress());
        Assert.assertEquals(account.getId(),1L);
    }

    @Test
    public void testGetAccountBySQL() throws Exception {
        Account account = accountDAO.getAccountByIdSQL(1L);
        System.out.println("Account Address: " + account.getAddress());
        Assert.assertEquals(account.getId(),1L);
    }

    @Test
    public void testGetAccountBySQLResultEntityObject() throws Exception {
        MappedEntity account = accountDAO.getAccountMappedEntityById(1L);
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

    @Test
    public void testGeneratedCreateAndDeleteGenerateAccount() throws Exception {
        int id = 9;
        Account account = DAOSupport.newEntityObject(Account.class);
        account.setId(id);
        account.setFirstName("YANG");
        account.setLastName("YONG");
        account.setMail("jfox.young@gmail.com");
        Query  query = accountDAO.createAutoInsertNativeQuery(Account.class);
        query.setParameter(Account.class.getSimpleName().toUpperCase(), account);
        int r = query.executeUpdate();
        Assert.assertEquals(r, 1);

        Query delQuery = accountDAO.createAutoDeleteByColumnNativeQuery(Account.class, "ACC_ID");
        delQuery.setParameter("ACC_ID", id);
        int r2 = delQuery.executeUpdate();
        Assert.assertEquals(r2, 1);
    }

    @Test
    public void testGeneratedUpdateAccount() throws Exception {
        int id = 8;
        Account account = DAOSupport.newEntityObject(Account.class);
        account.setId(id);
        account.setFirstName("YANG");
        account.setLastName("YONG");
        account.setMail("jfox.young@gmail.com");
        Query  query = accountDAO.createAutoUpdateNativeQuery(Account.class);
        query.setParameter("ACCOUNT", account);
        int r = query.executeUpdate();
        Assert.assertEquals(r, 1);
    }

    @Test
    public void testGeneratedSelectAccount() throws Exception {
        Query  query = accountDAO.createAutoSelectByIdNativeQuery(Account.class);
        query.setParameter("ACC_ID", 8);
        Object o = query.getSingleResult();
        System.out.println(o);
        Assert.assertNotNull(o);
    }
}
