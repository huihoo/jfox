package net.sourceforge.jfox.petstore.bo;

import java.util.List;

import net.sourceforge.jfox.petstore.entity.Item;
import net.sourceforge.jfox.petstore.entity.Order;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ItemBO {

    void updateQuantity(Order order);

    boolean isItemInStock(String itemId);

    List<Item> getItemListByProduct(String productId);

    Item getItem(String itemId);

}
