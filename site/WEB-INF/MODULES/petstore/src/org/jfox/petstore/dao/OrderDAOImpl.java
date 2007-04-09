package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;

import org.jfox.petstore.entity.LineItem;
import org.jfox.petstore.entity.Order;
import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.dao.DAOSupport;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */


@NamedNativeQueries(
        {
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_ORDER,
                query = "select " +
                        "billaddr1, " +
                        "billaddr2, " +
                        "billcity, " +
                        "billcountry, " +
                        "billstate, " +
                        "billtofirstname, " +
                        "billtolastname, " +
                        "billzip, " +
                        "shipaddr1, " +
                        "shipaddr2, " +
                        "shipcity, " +
                        "shipcountry, " +
                        "shipstate, " +
                        "shiptofirstname, " +
                        "shiptolastname, " +
                        "shipzip, " +
                        "cardtype, " +
                        "courier, " +
                        "creditcard, " +
                        "exprdate, " +
                        "locale, " +
                        "orders.orderdate as orderdate, " +
                        "orders.orderid as orderid, " +
                        "totalprice, " +
                        "userid, " +
                        "status " +
                        "from orders, orderstatus " +
                        "where orders.orderid = $orderid and orders.orderid = orderstatus.orderid",
                resultClass = Order.class
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_ORDERS_BY_USERNAME,
                query = "select " +
                        "billaddr1, " +
                        "billaddr2, " +
                        "billcity, " +
                        "billcountry, " +
                        "billstate, " +
                        "billtofirstname, " +
                        "billtolastname, " +
                        "billzip, " +
                        "shipaddr1, " +
                        "shipaddr2, " +
                        "shipcity, " +
                        "shipcountry, " +
                        "shipstate, " +
                        "shiptofirstname, " +
                        "shiptolastname, " +
                        "shipzip, " +
                        "cardtype, " +
                        "courier, " +
                        "creditcard, " +
                        "exprdate, " +
                        "locale, " +
                        "orders.orderdate as orderdate, " +
                        "orders.orderid as orderid, " +
                        "totalprice, " +
                        "userid, " +
                        "status " +
                        "from orders, orderstatus " +
                        "where " +
                        "orders.userid = $username and orders.orderid = orderstatus.orderid",
                resultClass = Order.class
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.INSERT_ORDER,
                query = "insert into orders (" +
                        "orderid, " +
                        "userid, " +
                        "orderdate, " +
                        "shipaddr1, " +
                        "shipaddr2, " +
                        "shipcity, " +
                        "shipstate, " +
                        "shipzip, " +
                        "shipcountry, " +
                        "billaddr1, " +
                        "billaddr2, " +
                        "billcity, " +
                        "billstate, " +
                        "billzip, " +
                        "billcountry, " +
                        "courier, " +
                        "totalprice, " +
                        "billtofirstname, " +
                        "billtolastname, " +
                        "shiptofirstname, " +
                        "shiptolastname, " +
                        "creditcard, " +
                        "exprdate, " +
                        "cardtype, " +
                        "locale" +
                        ") values (" +
                        "$order.getOrderId(), " +
                        "$order.getUsername(), " +
                        "$order.getOrderDate(), " +
                        "$order.getShipAddress1(), " +
                        "$order.getShipAddress2(), " +
                        "$order.getShipCity(), " +
                        "$order.getShipState(), " +
                        "$order.getShipZip(), " +
                        "$order.getShipCountry(), " +
                        "$order.getBillAddress1(), " +
                        "$order.getBillAddress2(), " +
                        "$order.getBillCity(), " +
                        "$order.getBillState(), " +
                        "$order.getBillZip(), " +
                        "$order.getBillCountry(), " +
                        "$order.getCourier(), " +
                        "$order.getTotalPrice(), " +
                        "$order.getBillToFirstName(), " +
                        "$order.getBillToLastName(), " +
                        "$order.getShipToFirstName(), " +
                        "$order.getShipToLastName(), " +
                        "$order.getCreditCard(), " +
                        "$order.getExpiryDate(), " +
                        "$order.getCardType(), " +
                        "$order.getLocale())"
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.INSERT_ORDER_STATUS,
                query = "insert into orderstatus (orderid, linenum, orderdate, status) values ($order.getOrderId(), $order.getOrderId(), $order.getOrderDate(), $order.getStatus())"
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.GET_LINEITEMS_BY_ORDERID,
                query = "select orderid, linenum, itemid, quantity, unitprice from lineitem where orderid = $orderid",
                resultClass = LineItem.class
        ),
        @NamedNativeQuery(
                name = OrderDAOImpl.INSERT_LINE_ITEM,
                query = "insert into lineitem (orderid, linenum, itemid, quantity, unitprice) values ($lineitem.getOrderId(), $lineitem.getLineNumber(), $lineitem.getItemId(), $lineitem.getQuantity(), $lineitem.getUnitPrice())"
        )
                }
)
@Stateless
@Local
@SuppressWarnings("unchecked")
public class OrderDAOImpl extends DAOSupport implements OrderDAO {

    public static final String INSERT_ORDER = "insertOrder";
    public static final String GET_ORDER = "getOrder";
    public static final String GET_ORDERS_BY_USERNAME = "getOrdersByUsername";
    public static final String INSERT_ORDER_STATUS = "insertOrderStatus";
    public static final String GET_LINEITEMS_BY_ORDERID = "getLineItemsByOrderId";
    public static final String INSERT_LINE_ITEM = "insertLineItem";

    @PersistenceContext(unitName = "JPetstoreMysqlDS")
    EntityManager em;


    protected EntityManager getEntityManager() {
        return (EntityManagerExt)em;
    }

    public Order getOrder(long orderId) throws SQLException {
        return (Order)createNamedNativeQuery(GET_ORDER).setParameter("orderid",orderId).getSingleResult();
    }

    public List<Order> getOrdersByUsername(String username) throws SQLException {
        return (List<Order>)createNamedNativeQuery(GET_ORDERS_BY_USERNAME).setParameter("username",username).getResultList();
    }

    public void insertOrder(Order order) throws SQLException {
            createNamedNativeQuery(INSERT_ORDER).setParameter("order", order).executeUpdate();
            createNamedNativeQuery(INSERT_ORDER_STATUS).setParameter("order", order).executeUpdate();
            for (LineItem lineItem : order.getLineItems()) {
                createNamedNativeQuery(INSERT_LINE_ITEM).setParameter("lineitem", lineItem).executeUpdate();
            }
    }

    public static void main(String[] args) {

    }
}
