package org.jfox.example.ejb3.entity;

import java.util.List;

import net.sourceforge.jfox.entity.dao.DataAccessObject;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface OrderDAO extends DataAccessObject {

    List<Order> getOrders();

    Order getOrder(int id);
}
