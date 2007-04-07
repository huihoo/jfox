package jfox.test.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class AddressTestMain {

    public static void main(String[] args) {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("default");
        final EntityManager em = emFactory.createEntityManager();
        Address address = (Address)em.createNativeQuery("select * from address where ADR_ID = $id", Address.class).setParameter("id",1).getSingleResult();
        System.out.println("Address: " + address);
        em.close();
        emFactory.close();
    }
}
