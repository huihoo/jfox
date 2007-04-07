package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import org.jfox.petstore.entity.Order;

public interface OrderDAO {

    List<Order> getOrdersByUsername(String username) throws SQLException;

    Order getOrder(long orderId) throws SQLException;

    void insertOrder(Order order) throws SQLException;

}
