/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import org.jfox.petstore.entity.Order;

public interface OrderDAO {

    List<Order> getOrdersByUsername(String username) throws SQLException;

    Order getOrder(long orderId) throws SQLException;

    void insertOrder(Order order) throws SQLException;

}
