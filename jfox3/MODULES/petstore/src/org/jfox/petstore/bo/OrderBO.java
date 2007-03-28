package org.jfox.petstore.bo;

import java.util.List;

import org.jfox.petstore.entity.Order;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface OrderBO {
    
    List<Order> getOrdersByUsername(String username);

    Order getOrder(long orderId);

    void insertOrder(Order order);
}
