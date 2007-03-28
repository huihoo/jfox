package org.jfox.petstore.bo;

import java.util.List;
import java.sql.SQLException;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.jfox.petstore.entity.Order;
import org.jfox.petstore.dao.OrderDAO;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless
@Local
public class OrderBOImpl implements OrderBO {

    @EJB
    OrderDAO orderDAO;

    public Order getOrder(long orderId) {
        try {
            return orderDAO.getOrder(orderId);
        }
        catch (SQLException e) {
            throw new EJBException(e);
        }
    }

    public List<Order> getOrdersByUsername(String username) {
        try {
            return orderDAO.getOrdersByUsername(username);
        }
        catch (SQLException e) {
            throw new EJBException(e);
        }
    }

    public void insertOrder(Order order) {
        try {
            orderDAO.insertOrder(order);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public static void main(String[] args) {

    }
}
