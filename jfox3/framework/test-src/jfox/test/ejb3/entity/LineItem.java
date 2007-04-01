package jfox.test.ejb3.entity;

import javax.persistence.Column;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface LineItem {
    @Column(name="id")
    Integer getId();
    void setId(Integer id);

    @Column(name="product")
    String getProduct();
    void setProduct(String product);

    @Column(name = "price")
    Double getPrice();
    void setPrice(Double price);

    @Column(name="quantity")
    int getQuantity();
    void setQuantity(int quantity);

    @Column(name="orderid")
    int getOrderId();
    void setOrderId(int orderId);
    
}
