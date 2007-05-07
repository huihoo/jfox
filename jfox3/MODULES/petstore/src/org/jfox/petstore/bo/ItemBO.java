/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
