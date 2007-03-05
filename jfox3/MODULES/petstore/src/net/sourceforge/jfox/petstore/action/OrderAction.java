package net.sourceforge.jfox.petstore.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;

import net.sourceforge.jfox.petstore.bo.OrderBO;
import net.sourceforge.jfox.petstore.domain.Cart;
import net.sourceforge.jfox.petstore.domain.CartItem;
import net.sourceforge.jfox.petstore.entity.Account;
import net.sourceforge.jfox.petstore.entity.LineItem;
import net.sourceforge.jfox.petstore.entity.Order;
import net.sourceforge.jfox.entity.dao.MapperEntity;
import net.sourceforge.jfox.entity.dao.PKgen;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.SessionContext;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.validate.LongValidation;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "order")
public class OrderAction extends ActionSupport {
    public static final String ORDER_SESSION_KEY = "__ORDER__";
    private static List<String> creditCardTypes = new ArrayList<String>();

    static {
        creditCardTypes.add("Visa");
        creditCardTypes.add("American Express");
        creditCardTypes.add("mobile c");
    }

    @EJB
    OrderBO orderBO;

    @ActionMethod(successView = "NewOrder.vhtml")
    public void doGetNew(InvocationContext invocationContext) throws Exception {
        SessionContext sessionContext = invocationContext.getSessionContext();
        Cart cart = (Cart)sessionContext.getAttribute(CartAction.CART_SESSION_KEY);

        PageContext pageContext = invocationContext.getPageContext();

        if(!sessionContext.containsAttribute(AccountAction.ACCOUNT_SESSION_KEY)) {
            // not login
            pageContext.setTargetView("signon.vhtml");
            return;
        }

        Account account = (Account)sessionContext.getAttribute(AccountAction.ACCOUNT_SESSION_KEY);

        Order order = MapperEntity.newEntityObject(Order.class);
        order.setOrderId(PKgen.getInstance().nextPK());
        order.setUsername(account.getUsername());
        order.setOrderDate(new Date());

        order.setShipToFirstName(account.getFirstName());
        order.setShipToLastName(account.getLastName());
        order.setShipAddress1(account.getAddress1());
        order.setShipAddress2(account.getAddress2());
        order.setShipCity(account.getCity());
        order.setShipState(account.getState());
        order.setShipZip(account.getZip());
        order.setShipCountry(account.getCountry());

        order.setBillToFirstName(account.getFirstName());
        order.setBillToLastName(account.getLastName());
        order.setBillAddress1(account.getAddress1());
        order.setBillAddress2(account.getAddress2());
        order.setBillCity(account.getCity());
        order.setBillState(account.getState());
        order.setBillZip(account.getZip());
        order.setBillCountry(account.getCountry());

        order.setTotalPrice(cart.getSubTotal());

        order.setCreditCard("999 9999 9999 9999");
        order.setExpiryDate("12/03");
        order.setCardType("Visa");
        order.setCourier("UPS");
        order.setLocale("CA");
        order.setStatus("P");

        List<LineItem> lineItems = new ArrayList<LineItem>();

        int i = 1;
        for (CartItem cartItem : cart.getCartItemList()) {
            LineItem lineItem = MapperEntity.newEntityObject(LineItem.class);
            lineItem.setLineNumber(i);
            lineItem.setOrderId(order.getOrderId());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setItemId(cartItem.getItem().getItemId());
            lineItem.setUnitPrice(cartItem.getItem().getListPrice());
            lineItem.setItem(cartItem.getItem());
            lineItems.add(lineItem);
            i++;
        }
        order.setLineItems(lineItems);

        sessionContext.setAttribute(ORDER_SESSION_KEY, order);

        pageContext.setAttribute("creditCardTypes", creditCardTypes);
        pageContext.setAttribute("order", order);
    }

    @ActionMethod(successView = "ConfirmOrder.vhtml", invocationClass = NewOrderInvocation.class)
    public void doPostNew(InvocationContext invocationContext) throws Exception {
        NewOrderInvocation invocation = (NewOrderInvocation)invocationContext.getInvocation();
        SessionContext sessionContext = invocationContext.getSessionContext();
//        Account account = (Account)sessionContext.getAttribute(AccountAction.ACCOUNT_SESSION_KEY);
        Order order = (Order)sessionContext.getAttribute(ORDER_SESSION_KEY);

        order.setShipToFirstName(invocation.getBillToFirstName());
        order.setShipToLastName(invocation.getBillToLastName());
        order.setShipAddress1(invocation.getBillAddress1());
        order.setShipAddress2(invocation.getBillAddress2());
        order.setShipCity(invocation.getBillCity());
        order.setShipState(invocation.getBillState());
        order.setShipZip(invocation.getBillZip());
        order.setShipCountry(invocation.getBillCountry());

        order.setBillToFirstName(invocation.getBillToFirstName());
        order.setBillToLastName(invocation.getBillToLastName());
        order.setBillAddress1(invocation.getBillAddress1());
        order.setBillAddress2(invocation.getBillAddress2());
        order.setBillCity(invocation.getBillCity());
        order.setBillState(invocation.getBillState());
        order.setBillZip(invocation.getBillZip());
        order.setBillCountry(invocation.getBillCountry());

        order.setCardType(invocation.getCardType());
        order.setCreditCard(invocation.getCreditCard());

        if ("1".equals(invocation.getShippingAddressRequired())) {
            // don't need input shipping address
            invocationContext.getPageContext().setTargetView("Shipping.vhtml");
        }

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("order", order);
    }

    @ActionMethod(successView = "ConfirmOrder.vhtml", invocationClass = ShippingOrderInvocation.class)
    public void doPostConfirmShipping(InvocationContext invocationContext) throws Exception {
        ShippingOrderInvocation invocation = (ShippingOrderInvocation)invocationContext.getInvocation();
        SessionContext sessionContext = invocationContext.getSessionContext();
        Order order = (Order)sessionContext.getAttribute(ORDER_SESSION_KEY);

        order.setShipToFirstName(invocation.getShipToFirstName());
        order.setShipToLastName(invocation.getShipToLastName());
        order.setShipAddress1(invocation.getShipAddress1());
        order.setShipAddress2(invocation.getShipAddress2());
        order.setShipCity(invocation.getShipCity());
        order.setShipState(invocation.getShipState());
        order.setShipZip(invocation.getShipZip());
        order.setShipCountry(invocation.getShipCountry());

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("order", order);
    }

    @ActionMethod(successView = "ViewOrder.vhtml", errorView = "Cart.vhtml")
    public void doGetConfirm(InvocationContext invocationContext) throws Exception {
        SessionContext sessionContext = invocationContext.getSessionContext();
        PageContext pageContext = invocationContext.getPageContext();

        Order order = (Order)sessionContext.getAttribute(ORDER_SESSION_KEY);
        if (order != null) {
            //insert order
            try {
                orderBO.insertOrder(order);

                sessionContext.removeAttribute(ORDER_SESSION_KEY);
                sessionContext.removeAttribute(CartAction.CART_SESSION_KEY);
                pageContext.setAttribute("order", order);
            }
            catch (Exception e) {
                Cart cart = (Cart)sessionContext.getAttribute(CartAction.CART_SESSION_KEY);
                pageContext.setAttribute("cart", cart);
                throw e;
            }
        }
    }

    @ActionMethod(successView = "ListOrders.vhtml")
    public void doGetList(InvocationContext invocationContext) throws Exception {
        SessionContext sessionContext = invocationContext.getSessionContext();
        Account account = (Account)sessionContext.getAttribute(AccountAction.ACCOUNT_SESSION_KEY);
        List<Order> orders = orderBO.getOrdersByUsername(account.getUsername());

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("orders",orders);
    }

    @ActionMethod(successView = "ViewOrder.vhtml", invocationClass = ViewOrderInvocation.class)
    public void doGetView(InvocationContext invocationContext) throws Exception {
        ViewOrderInvocation invocation = (ViewOrderInvocation)invocationContext.getInvocation();
        long orderId = invocation.getOrderId();
        Order order = orderBO.getOrder(orderId);
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("order",order);
    }

    public static class ViewOrderInvocation extends Invocation {

        @LongValidation
        private long orderId;

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }
    }

    public static class NewOrderInvocation extends Invocation {
        private String cardType;
        private String creditCard;
        private String expiryDate;
        private String billToFirstName;
        private String billToLastName;
        private String billAddress1;
        private String billAddress2;
        private String billCity;
        private String billState;
        private String billZip;
        private String billCountry;
        private String shippingAddressRequired;

        public String getBillAddress1() {
            return billAddress1;
        }

        public void setBillAddress1(String billAddress1) {
            this.billAddress1 = billAddress1;
        }

        public String getBillAddress2() {
            return billAddress2;
        }

        public void setBillAddress2(String billAddress2) {
            this.billAddress2 = billAddress2;
        }

        public String getBillCity() {
            return billCity;
        }

        public void setBillCity(String billCity) {
            this.billCity = billCity;
        }

        public String getBillCountry() {
            return billCountry;
        }

        public void setBillCountry(String billCountry) {
            this.billCountry = billCountry;
        }

        public String getBillState() {
            return billState;
        }

        public void setBillState(String billState) {
            this.billState = billState;
        }

        public String getBillToFirstName() {
            return billToFirstName;
        }

        public void setBillToFirstName(String billToFirstName) {
            this.billToFirstName = billToFirstName;
        }

        public String getBillToLastName() {
            return billToLastName;
        }

        public void setBillToLastName(String billToLastName) {
            this.billToLastName = billToLastName;
        }

        public String getBillZip() {
            return billZip;
        }

        public void setBillZip(String billZip) {
            this.billZip = billZip;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getCreditCard() {
            return creditCard;
        }

        public void setCreditCard(String creditCard) {
            this.creditCard = creditCard;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getShippingAddressRequired() {
            return shippingAddressRequired;
        }

        public void setShippingAddressRequired(String shippingAddressRequired) {
            this.shippingAddressRequired = shippingAddressRequired;
        }
    }

    public static class ShippingOrderInvocation extends Invocation {
        private String shipToFirstName;
        private String shipToLastName;
        private String shipAddress1;
        private String shipAddress2;
        private String shipCity;
        private String shipState;
        private String shipZip;
        private String shipCountry;

        public String getShipAddress1() {
            return shipAddress1;
        }

        public void setShipAddress1(String shipAddress1) {
            this.shipAddress1 = shipAddress1;
        }

        public String getShipAddress2() {
            return shipAddress2;
        }

        public void setShipAddress2(String shipAddress2) {
            this.shipAddress2 = shipAddress2;
        }

        public String getShipCity() {
            return shipCity;
        }

        public void setShipCity(String shipCity) {
            this.shipCity = shipCity;
        }

        public String getShipCountry() {
            return shipCountry;
        }

        public void setShipCountry(String shipCountry) {
            this.shipCountry = shipCountry;
        }

        public String getShipState() {
            return shipState;
        }

        public void setShipState(String shipState) {
            this.shipState = shipState;
        }

        public String getShipToFirstName() {
            return shipToFirstName;
        }

        public void setShipToFirstName(String shipToFirstName) {
            this.shipToFirstName = shipToFirstName;
        }

        public String getShipToLastName() {
            return shipToLastName;
        }

        public void setShipToLastName(String shipToLastName) {
            this.shipToLastName = shipToLastName;
        }

        public String getShipZip() {
            return shipZip;
        }

        public void setShipZip(String shipZip) {
            this.shipZip = shipZip;
        }

    }

    public static void main(String[] args) {

    }
}
