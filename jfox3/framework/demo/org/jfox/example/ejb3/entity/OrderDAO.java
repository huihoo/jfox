package org.jfox.example.ejb3.entity;

import java.util.List;

import net.sourceforge.jfox.entity.dao.DataAccessObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface OrderDAO extends DataAccessObject {

    List<Order> getOrders();

    Order getOrder(int id);
}
