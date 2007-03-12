package org.jfox.example.ejb3.entity;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;

import net.sourceforge.jfox.entity.dao.DAOSupport;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_LINEITEM_BY_ID,
                query = "select * from lineitem where id=$id",
                resultClass = LineItem.class
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_LINEITEMS_BY_ORDER_ID,
                query = "select * from lineitem where orderid=$orderid",
                resultClass = LineItem.class
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_ORDER_BY_ID,
                query = "select * from orders where id=$id",
                resultClass = Order.class
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_ORDERS,
                query = "select * from orders",
                resultClass = Order.class
        )
                })
@Stateless(name="entity.OrderDAO")
public class OrderDAOImpl extends DAOSupport implements OrderDAO {

    public static final String GET_LINEITEM_BY_ID = "GET_LINEITEM_BY_ID";
    public static final String GET_LINEITEMS_BY_ORDER_ID = "GET_LINEITEMs_BY_ORDER_ID";
    public static final String GET_ORDER_BY_ID = "GET_ORDER_BY_ID";
    public static final String GET_ORDERS = "GET_ORDERS";

    @PersistenceContext(unitName = "default")
    EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Order> getOrders(){
        return (List<Order>)createNamedNativeQuery(GET_ORDERS).getResultList();
    }

    public Order getOrder(int id) {
        return (Order)createNamedNativeQuery(GET_ORDER_BY_ID).setParameter("id",id).getSingleResult();
    }

}
