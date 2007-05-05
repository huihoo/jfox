package jfox.test.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class AddressTestMain {

    public static EntityManager em;

    @BeforeClass
    public static void setup(){
        // initialize EntityManager by persistence unit
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("default");
        em = emFactory.createEntityManager();
    }

    @AfterClass
    public static void teardown(){
        em.close();
    }

    @Test
    public void testAddress() {
        // query
        Address address = (Address)em.createNativeQuery("select * from address where ADR_ID = $id", Address.class).setParameter("id",1).getSingleResult();
        System.out.println("Address: " + address);
    }

    @Test
    public void testAccount(){
        List<Account> accounts = em.createNativeQuery("select * from account", Account.class).getResultList();
        for(Account account : accounts){
            System.out.println(account);
        }
    }
}
