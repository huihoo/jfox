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
