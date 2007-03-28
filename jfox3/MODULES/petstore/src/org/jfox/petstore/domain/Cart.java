package org.jfox.petstore.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.jfox.petstore.entity.Item;

/**
 * Cart is not a Entity Object, just a logic domain Object contain CartItem
 */
public class Cart implements Serializable {

    // itemId => CartItem
    private final Map<String, CartItem> itemMap = Collections.synchronizedMap(new TreeMap<String, CartItem>());

    public Collection<CartItem> getCartItemList() {
        return itemMap.values();
    }

    public CartItem getCartItem(String itemId){
        return itemMap.get(itemId);
    }

    public int getNumberOfItems() {
        return itemMap.size();
    }

    public boolean containsItemId(String itemId) {
        return itemMap.containsKey(itemId);
    }

    public void addItem(Item item, boolean isInStock) {
        CartItem cartItem = itemMap.get(item.getItemId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setQuantity(0);
            cartItem.setInStock(isInStock);
            itemMap.put(item.getItemId(), cartItem);
        }
        cartItem.incrementQuantity();
    }


    public Item removeItemById(String itemId) {
        CartItem cartItem = itemMap.remove(itemId);
        if (cartItem == null) {
            return null;
        }
        else {
            return cartItem.getItem();
        }
    }

    public void incrementQuantityByItemId(String itemId) {
        CartItem cartItem = itemMap.get(itemId);
        cartItem.incrementQuantity();
    }

    public void setQuantityByItemId(String itemId, int quantity) {
        CartItem cartItem = itemMap.get(itemId);
        cartItem.setQuantity(quantity);
    }

    public double getSubTotal() {
        double subTotal = 0;
        for(CartItem cartItem : itemMap.values()){
            Item item = cartItem.getItem();
            double listPrice = item.getListPrice();
            int quantity = cartItem.getQuantity();
            subTotal += listPrice * quantity;
        }
        return subTotal;
    }
}
