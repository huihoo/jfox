/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.domain;

import java.io.Serializable;

import org.jfox.petstore.entity.Item;

/**
 * CartItem is not a Entity Object, just a logic domian Object contain Item and it's quantity
 */
public class CartItem implements Serializable {

    /* Private Fields */

    private Item item;
    private int quantity;
    private boolean inStock;

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        if (item != null) {
            return item.getListPrice() * quantity;
        }
        else {
            return 0;
        }
    }

    public void incrementQuantity() {
        quantity++;
    }

}
