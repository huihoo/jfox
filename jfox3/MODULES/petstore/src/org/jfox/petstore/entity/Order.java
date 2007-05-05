package org.jfox.petstore.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Column;

import org.jfox.entity.annotation.MappingColumn;
import org.jfox.entity.annotation.ParameterMap;
import org.jfox.petstore.dao.OrderDAOImpl;

@Entity
public class Order implements Serializable {

    @Column(name = "orderid")
    long orderId;

    @Column(name = "userid")
    String username;

    @Column(name = "orderdate")
    Date orderDate;

    @Column(name = "shipaddr1")
    String shipAddress1;

    @Column(name = "shipaddr2")
    String shipAddress2;

    @Column(name = "shipcity")
    String shipCity;

    @Column(name = "shipstate")
    String shipState;

    @Column(name = "shipzip")
    String shipZip;

    @Column(name = "shipcountry")
    String shipCountry;

    @Column(name = "billaddr1")
    String billAddress1;

    @Column(name = "billaddr2")
    String billAddress2;

    @Column(name = "billcity")
    String billCity;

    @Column(name = "billstate")
    String billState;

    @Column(name = "billzip")
    String billZip;

    @Column(name = "billcountry")
    String billCountry;

    @Column(name = "courier")
    String courier;

    @Column(name = "totalprice")
    double totalPrice;

    @Column(name = "billtofirstname")
    String billToFirstName;

    @Column(name = "billtolastname")
    String billToLastName;

    @Column(name = "shiptofirstname")
    String shipToFirstName;

    @Column(name = "shiptolastname")
    String shipToLastName;

    @Column(name = "creditcard")
    String creditCard;

    @Column(name = "exprdate")
    String expiryDate;

    @Column(name = "cardtype")
    String cardType;

    @Column(name = "locale")
    String locale;


    // orderstatus table
    @Column(name = "status")
    String status;

    //MappedColumn
    @MappingColumn(namedQuery = OrderDAOImpl.GET_LINEITEMS_BY_ORDERID, params = {@ParameterMap(name = "orderid", value = "$this.getOrderId()")})
    List<LineItem> lineItems;


    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

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

    public String getShipState() {
        return shipState;
    }

    public void setShipState(String shipState) {
        this.shipState = shipState;
    }

    public String getShipZip() {
        return shipZip;
    }

    public void setShipZip(String shipZip) {
        this.shipZip = shipZip;
    }

    public String getShipCountry() {
        return shipCountry;
    }

    public void setShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
    }

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

    public String getBillState() {
        return billState;
    }

    public void setBillState(String billState) {
        this.billState = billState;
    }

    public String getBillZip() {
        return billZip;
    }

    public void setBillZip(String billZip) {
        this.billZip = billZip;
    }

    public String getBillCountry() {
        return billCountry;
    }

    public void setBillCountry(String billCountry) {
        this.billCountry = billCountry;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
