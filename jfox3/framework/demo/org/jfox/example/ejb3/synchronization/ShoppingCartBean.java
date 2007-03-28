package org.jfox.example.ejb3.synchronization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.rmi.RemoteException;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.EJBException;
import javax.annotation.Resource;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Stateful(name = "sychronization.ShoppingCartBean")
public class ShoppingCartBean implements ShoppingCart, Serializable, SessionSynchronization {

    private Map<String, Integer> cart = new HashMap<String, Integer>();

    @Resource
    SessionContext sessionContext;

    public void buy(String product, int quantity) {
        if (cart.containsKey(product)) {
            int currq = cart.get(product);
            currq += quantity;
            cart.put(product, currq);
        }
        else {
            cart.put(product, quantity);
        }
    }

    public Map<String, Integer> getCartContents() {
        return cart;
    }

    @Remove
    public void checkout() {
        System.out.println("To be implemented");
    }

    public void afterBegin() throws EJBException, RemoteException {
        System.out.println("afterBegin...");
    }

    public void afterCompletion(boolean committed) throws EJBException, RemoteException {
        System.out.println("afterCompletion: " + committed);
    }

    public void beforeCompletion() throws EJBException, RemoteException {
        System.out.println("beforeCompletion...");
    }
}

