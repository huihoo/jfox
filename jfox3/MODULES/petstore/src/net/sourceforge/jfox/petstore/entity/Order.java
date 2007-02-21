package net.sourceforge.jfox.petstore.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Column;

import net.sourceforge.jfox.entity.annotation.MappedColumn;
import net.sourceforge.jfox.entity.annotation.ParameterMap;
import net.sourceforge.jfox.petstore.dao.OrderDAOImpl;

@Entity
public interface Order extends Serializable {

    @Column(name = "orderid")
    long getOrderId();
	void setOrderId(long orderId);

    @Column(name = "userid")
    String getUsername();
	void setUsername(String username);

    @Column(name = "orderdate")
    Date getOrderDate();
	void setOrderDate(Date orderDate);

    @Column(name = "shipaddr1")
    String getShipAddress1();
	void setShipAddress1(String shipAddress1);

    @Column(name = "shipaddr2")
    String getShipAddress2();
	void setShipAddress2(String shipAddress2);

    @Column(name = "shipcity")
    String getShipCity();
	void setShipCity(String shipCity);

    @Column(name = "shipstate")
    String getShipState();
	void setShipState(String shipState);

    @Column(name = "shipzip")
    String getShipZip();
	void setShipZip(String shipZip);

    @Column(name = "shipcountry")
    String getShipCountry();
	void setShipCountry(String shipCountry);

    @Column(name = "billaddr1")
    String getBillAddress1();
	void setBillAddress1(String billAddress1);

    @Column(name = "billaddr2")
    String getBillAddress2();
	void setBillAddress2(String billAddress2);

    @Column(name = "billcity")
    String getBillCity();
	void setBillCity(String billCity);

    @Column(name = "billstate")
    String getBillState();
	void setBillState(String billState);

    @Column(name = "billzip")
    String getBillZip();
	void setBillZip(String billZip);

    @Column(name = "billcountry")
    String getBillCountry();
	void setBillCountry(String billCountry);

    @Column(name = "courier")
    String getCourier();
	void setCourier(String courier);

    @Column(name = "totalprice")
    double getTotalPrice();
	void setTotalPrice(double totalPrice);

    @Column(name = "billtofirstname")
    String getBillToFirstName();
	void setBillToFirstName(String billToFirstName);

    @Column(name = "billtolastname")
    String getBillToLastName();
	void setBillToLastName(String billToLastName);

    @Column(name = "shiptofirstname")
    String getShipToFirstName();
	void setShipToFirstName(String shipFoFirstName);

    @Column(name = "shiptolastname")
    String getShipToLastName();
	void setShipToLastName(String shipToLastName);

    @Column(name = "creditcard")
    String getCreditCard();
	void setCreditCard(String creditCard);

    @Column(name = "exprdate")
    String getExpiryDate();
	void setExpiryDate(String expiryDate);

    @Column(name = "cardtype")
    String getCardType();
	void setCardType(String cardType);

    @Column(name = "locale")
    String getLocale();
	void setLocale(String locale);


    // orderstatus table
    @Column(name = "status")
    String getStatus();
	void setStatus(String status);

    //MappedColumn
    @MappedColumn(namedQuery = OrderDAOImpl.GET_LINEITEMS_BY_ORDERID, params = {@ParameterMap(name = "orderid", value = "$this.getOrderId()")})
    List<LineItem> getLineItems();
    void setLineItems(List<LineItem> lineItems);

}
