package net.sourceforge.jfox.petstore.domain;

import java.io.Serializable;

import net.sourceforge.jfox.petstore.entity.Item;

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
