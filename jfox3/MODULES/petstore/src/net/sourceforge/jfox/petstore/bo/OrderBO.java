package net.sourceforge.jfox.petstore.bo;

import java.util.List;

import net.sourceforge.jfox.petstore.entity.Order;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public interface OrderBO {
    
    List<Order> getOrdersByUsername(String username);

    Order getOrder(long orderId);

    void insertOrder(Order order);
}
