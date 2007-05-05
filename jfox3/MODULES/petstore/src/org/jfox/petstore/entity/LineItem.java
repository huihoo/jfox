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
public class LineItem implements Serializable {

    @Column(name = "orderid")
    long orderId;

    @Column(name = "linenum")
    int lineNumber;

    @Column(name = "itemid")
    String itemId;

    @Column(name = "unitprice")
    double unitPrice;

    @Column(name = "quantity")
    int quantity;

    @MappingColumn(namedQuery = ItemDAOImpl.GET_ITEM, params = {@ParameterMap(name = "id",value = "$this.getItemId()")})
    Item item;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public static class Helper {
        public static double getTotalPrice(LineItem lineItem) {
            return lineItem.getUnitPrice() * lineItem.getQuantity();
        }
    }
}
