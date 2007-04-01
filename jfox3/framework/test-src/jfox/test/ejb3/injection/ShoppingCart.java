package jfox.test.ejb3.injection;

import java.util.Map;

public interface ShoppingCart {

    void buy(String product, int quantity);

    Map<String, Integer> getCartContents();

}
