/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import org.jfox.petstore.entity.Item;
import org.jfox.petstore.entity.Order;

public interface ItemDAO {

    void updateQuantity(Order order) throws SQLException;

    int getInventoryQuantity(String itemId) throws SQLException;

    List<Item> getItemListByProduct(String productId) throws SQLException;

    Item getItem(String itemId) throws SQLException;

}
