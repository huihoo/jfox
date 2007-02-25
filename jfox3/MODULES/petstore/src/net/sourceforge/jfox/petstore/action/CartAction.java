package net.sourceforge.jfox.petstore.action;

import javax.ejb.EJB;

import net.sourceforge.jfox.petstore.bo.ItemBO;
import net.sourceforge.jfox.petstore.domain.Cart;
import net.sourceforge.jfox.petstore.domain.CartItem;
import net.sourceforge.jfox.petstore.entity.Item;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.SessionContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "cart")
public class CartAction extends ActionSupport {

    public static String CART_SESSION_KEY = "__CART__";

    @EJB
    ItemBO itemBO;

    @ActionMethod(successView = "Cart.vhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception {
        SessionContext sessionContext = invocationContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            sessionContext.setAttribute(CART_SESSION_KEY, cart);
        }

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("cart", cart);
    }

    @ActionMethod(successView = "Cart.vhtml", invocationClass = CartInvocation.class)
    public void doGetAddItem(InvocationContext invocationContext) throws Exception {
        CartInvocation invocation = (CartInvocation)invocationContext.getInvocation();

        SessionContext sessionContext = invocationContext.getSessionContext();
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

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("cart", cart);
    }

    @ActionMethod(successView = "Cart.vhtml", invocationClass = CartInvocation.class)
    public void doGetRemoveItem(InvocationContext invocationContext) throws Exception {
        CartInvocation invocation = (CartInvocation)invocationContext.getInvocation();
        SessionContext sessionContext = invocationContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            sessionContext.setAttribute(CART_SESSION_KEY, cart);
        }
        if (cart.containsItemId(invocation.getWorkingItemId())) {
            cart.removeItemById(invocation.getWorkingItemId());
        }

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("cart", cart);

    }

    @ActionMethod(successView = "Cart.vhtml", invocationClass = Invocation.class)
    public void doPostUpdate(InvocationContext invocationContext) throws Exception {
        Invocation invocation = invocationContext.getInvocation();
        SessionContext sessionContext = invocationContext.getSessionContext();
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

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("cart", cart);

    }

    @ActionMethod(successView = "Checkout.vhtml")
    public void doGetCheckout(InvocationContext invocationContext) throws Exception{
        SessionContext sessionContext = invocationContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CART_SESSION_KEY);
        PageContext pageContext = invocationContext.getPageContext();
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
