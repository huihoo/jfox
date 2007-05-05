package org.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.jfox.entity.annotation.MappingColumn;
import org.jfox.entity.annotation.ParameterMap;
import org.jfox.petstore.dao.ItemDAOImpl;

/**
 * IineItem is a order line item.
 */
@Entity
public interface LineItem extends Serializable {

    @Column(name = "orderid")
    public long getOrderId();
    public void setOrderId(long orderId);

    @Column(name = "linenum")
    public int getLineNumber();
    public void setLineNumber(int lineNumber);

    @Column(name = "itemid")
    public String getItemId();
    public void setItemId(String itemId);

    @Column(name = "unitprice")
    public double getUnitPrice();
    public void setUnitPrice(double unitprice);

    @Column(name = "quantity")
    public int getQuantity();
    public void setQuantity(int quantity);

    @MappingColumn(namedQuery = ItemDAOImpl.GET_ITEM, params = {@ParameterMap(name = "id",value = "$this.getItemId()")})
    public Item getItem();
    public void setItem(Item item);

    public static class Helper {
        public static double getTotalPrice(LineItem lineItem) {
            return lineItem.getUnitPrice() * lineItem.getQuantity();
        }
    }
}
