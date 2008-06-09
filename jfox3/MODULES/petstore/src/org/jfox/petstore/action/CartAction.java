/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.action;

import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.petstore.bo.ItemBO;
import org.jfox.petstore.domain.Cart;
import org.jfox.petstore.domain.CartItem;
import org.jfox.petstore.entity.Item;

import javax.ejb.EJB;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name = "cart")
public class CartAction extends ActionSupport {

    public static String CART_SESSION_KEY = "__CART__";

    @EJB
    ItemBO itemBO;

    @ActionMethod(name="view", successView = "Cart.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetView(ActionContext actionContext) throws Exception {
        SessionContext sessionContext = actionContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            sessionContext.setAttribute(CART_SESSION_KEY, cart);
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("cart", cart);
    }

    @ActionMethod(name="additem", successView = "Cart.vhtml", invocationClass = CartInvocation.class, httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetAddItem(ActionContext actionContext) throws Exception {
        CartInvocation invocation = (CartInvocation)actionContext.getInvocation();

        SessionContext sessionContext = actionContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            sessionContext.setAttribute(CART_SESSION_KEY, cart);
        }
        if (!cart.containsItemId(invocation.getWorkingItemId())) {
            Item item = itemBO.getItem(invocation.getWorkingItemId());
            cart.addItem(item, itemBO.isItemInStock(invocation.getWorkingItemId()));
        }
        else {
            cart.incrementQuantityByItemId(invocation.getWorkingItemId());
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("cart", cart);
    }

    @ActionMethod(name="removeitem", successView = "Cart.vhtml", invocationClass = CartInvocation.class, httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetRemoveItem(ActionContext actionContext) throws Exception {
        CartInvocation invocation = (CartInvocation)actionContext.getInvocation();
        SessionContext sessionContext = actionContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            sessionContext.setAttribute(CART_SESSION_KEY, cart);
        }
        if (cart.containsItemId(invocation.getWorkingItemId())) {
            cart.removeItemById(invocation.getWorkingItemId());
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("cart", cart);

    }

    @ActionMethod(name="update", successView = "Cart.vhtml", invocationClass = Invocation.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostUpdate(ActionContext actionContext) throws Exception {
        Invocation invocation = actionContext.getInvocation();
        SessionContext sessionContext = actionContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            sessionContext.setAttribute(CART_SESSION_KEY, cart);
        }
        for (String key : invocation.attributeKeys()) {
            CartItem cartItem = cart.getCartItem(key);
            if (cartItem != null) {
                try {
                    int quantity = Integer.parseInt((String)invocation.getAttribute(key));
                    cartItem.setQuantity(quantity);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("cart", cart);

    }

    @ActionMethod(name="checkout", successView = "Checkout.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetCheckout(ActionContext actionContext) throws Exception{
        SessionContext sessionContext = actionContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("cart", cart);
    }

    public static class CartInvocation extends Invocation {
        private String workingItemId;

        public String getWorkingItemId() {
            return workingItemId;
        }

        public void setWorkingItemId(String workingItemId) {
            this.workingItemId = workingItemId;
        }
    }

    public static void main(String[] args) {

    }
}
