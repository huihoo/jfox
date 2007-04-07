package jfox.test.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class AddressTestMain {

    public static void main(String[] args) {
        // initialize EntityManager by persistence unit
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("default");
        final EntityManager em = emFactory.createEntityManager();

        // query
        Address address = (Address)em.createNativeQuery("select * from address where ADR_ID = $id", Address.class).setParameter("id",1).getSingleResult();
        System.out.println("Address: " + address);

        // close EntityManager, EntityManagerFactory
        em.close();
        emFactory.close();
    }
}
