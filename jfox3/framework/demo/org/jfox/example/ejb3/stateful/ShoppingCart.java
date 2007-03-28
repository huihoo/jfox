package org.jfox.example.ejb3.stateful;

import java.util.Map;
import javax.ejb.Remove;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ShoppingCart {

    void buy(String product, int quantity);

    Map<String, Integer> getCartContents();

    @Remove
    void checkout();
}