package jfox.test.ejb3.entity;

import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.support.EntitySupport;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-1-31 18:49:57
 */
public class OrderDAOTest {

     static OrderDAO accountDAO = null;

    @BeforeClass
    public static void setup() throws Exception {

        final EntityManager testem = Persistence.createEntityManagerFactory("TestMysqlDS").createEntityManager();
        accountDAO = new OrderDAOImpl(){
            public EntityManager getEntityManager() {
                return (EntityManagerExt)testem;
            }
        };
    }

    @Test
    public void getAllOrders() throws Exception{
        List<Order> orders = accountDAO.getOrders();
        for(Order order : orders){
            System.out.println(EntitySupport.toString(order));
        }

    }
}
