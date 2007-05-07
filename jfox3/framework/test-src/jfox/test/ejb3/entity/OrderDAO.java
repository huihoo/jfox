/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.entity;

import java.util.List;

import org.jfox.entity.dao.DataAccessObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface OrderDAO extends DataAccessObject {

    List<Order> getOrders();

    Order getOrder(int id);
}
