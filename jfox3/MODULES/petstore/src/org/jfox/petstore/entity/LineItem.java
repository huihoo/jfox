/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * IineItem is a order line item.
 */
@Entity
@Table(name = "lineitem")
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

    @JoinColumn(columnDefinition = "select i.itemid, listprice, unitcost, supplier, i.productid, name, descn, category, status, attr1, attr2, attr3, attr4, attr5, qty from item i, inventory v, product p where p.productid = i.productid and i.itemid = v.itemid and i.itemid = $itemid")
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
