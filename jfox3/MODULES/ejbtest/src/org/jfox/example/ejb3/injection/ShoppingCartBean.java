package org.jfox.example.ejb3.injection;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;


@Stateful(name="injection.ShoppingCartBean")
@Remote(ShoppingCart.class)
public class ShoppingCartBean implements ShoppingCart, java.io.Serializable {
    private final HashMap<String, Integer> cart = new HashMap<String, Integer>();

    @EJB(beanName = "injection.CalculatorBean")
    private Calculator calculator;

    public void buy(String product, int quantity) {
        if (cart.containsKey(product)) {
            int currq = cart.get(product);
            currq = calculator.add(currq, quantity);
            cart.put(product, currq);
        }
        else {
            cart.put(product, quantity);
        }
    }

    public Map<String, Integer> getCartContents() {
        return cart;
    }

}
