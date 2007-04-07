package org.jfox.petstore.bo;

import java.util.List;

import org.jfox.petstore.entity.Item;
import org.jfox.petstore.entity.Order;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ItemBO {

    void updateQuantity(Order order);

    boolean isItemInStock(String itemId);

    List<Item> getItemListByProduct(String productId);

    Item getItem(String itemId);

}
