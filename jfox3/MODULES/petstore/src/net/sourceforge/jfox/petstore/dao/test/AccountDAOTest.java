package net.sourceforge.jfox.petstore.dao.test;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import net.sourceforge.jfox.entity.EntityManagerExt;
import net.sourceforge.jfox.entity.dao.DAOSupport;
import net.sourceforge.jfox.petstore.dao.AccountDAO;
import net.sourceforge.jfox.petstore.dao.AccountDAOImpl;
import net.sourceforge.jfox.petstore.entity.Account;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class AccountDAOTest {
    
    static AccountDAO accountDAO = null;


    @BeforeClass
    public static void setup() throws Exception {
        final EntityManager em = Persistence.createEntityManagerFactory("JPetstoreMysqlDS").createEntityManager();

        accountDAO = new AccountDAOImpl(){
            public EntityManager getEntityManager() {
                return (EntityManagerExt)em;
            }
        };
    }

    @Test
    public void testGetUsernameList() throws Exception {
        List<String> usernames = accountDAO.getUsernameList();
        System.out.println(Arrays.toString(usernames.toArray(new String[usernames.size()])));
    }
    
    @Test
    public void testGetAccountByUsername() throws Exception {
        Account account = accountDAO.getAccount("j2ee");
        System.out.println("Account: " + account);
        System.out.println("bannerOptions:  " + Account.AccountHelper.isBannerOption(account));
    }

    @Test
    public void testGetAccountByUsernameAndPassword() throws Exception {
        Account account = accountDAO.getAccount("ACID","ACID");
        System.out.println("Account: " + account);

    }
    
    @Test
    public void testCreateAccount() throws Exception {
//        ((DAOSupport)accountDAO).getEntityManager().getTransaction().begin();
        Account account = DAOSupport.newEntityObject(Account.class);
        account.setAddress1("Haidian");
        account.setAddress2("DongWangzhuang");
        account.setBannerName("yangyong");
        account.setBannerOption(1);
        account.setCity("Beijing");
        account.setCountry("China");
        account.setEmail("yangyong@live.com");
        account.setFavouriteCategoryId("TestCategoryId");
        account.setFirstName("Yang");
        account.setLanguagePreference("chinese");
        account.setLastName("Yong");
        account.setListOption(1);
        account.setPassword("YY");
        account.setPhone("13988888888");
        account.setState("Hunan");
        account.setStatus("OK");
        account.setUsername("YangYong");
        account.setZip("1234567");
        accountDAO.insertAccount(account);
//        ((DAOSupport)accountDAO).getEntityManager().getTransaction().commit();
    }

    public static void main(String[] args) {

    }
}
