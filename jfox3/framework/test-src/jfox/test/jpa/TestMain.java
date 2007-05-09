/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
public class TestMain {

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
        List<Account> accounts = em.createNativeQuery("select * from account", Account.class).setFirstResult(0).setMaxResults(2).getResultList();
        for(Account account : accounts){
            System.out.println(account);
        }
    }
}
