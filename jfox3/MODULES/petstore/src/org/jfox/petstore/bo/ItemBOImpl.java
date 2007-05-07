/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.bo;

import java.util.List;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.jfox.petstore.entity.Item;
import org.jfox.petstore.entity.Order;
import org.jfox.petstore.dao.ItemDAO;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless
@Local
public class ItemBOImpl implements ItemBO {

    @EJB
    ItemDAO itemDAO;

    public List<Item> getItemListByProduct(String productId) {
        try {
            return itemDAO.getItemListByProduct(productId);
        }
        catch(SQLException e){
            throw new EJBException(e);
        }
    }

    public Item getItem(String itemId) {
        try {
            return itemDAO.getItem(itemId);
        }
        catch(Exception e) {
            return null;
        }
    }

    public boolean isItemInStock(String itemId) {
        try {
            return itemDAO.getInventoryQuantity(itemId) > 0;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void updateQuantity(Order order) {
    }

    public static void main(String[] args) {

    }
}
