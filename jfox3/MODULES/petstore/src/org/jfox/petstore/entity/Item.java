package org.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Column;

import org.jfox.entity.annotation.MappingColumn;
import org.jfox.entity.annotation.ParameterMap;
import org.jfox.petstore.dao.ProductDAOImpl;

/**
 * Item means a item of a product
 */
@Entity
public interface Item extends Serializable {

    @Column(name="itemid")
    public String getItemId();
    public void setItemId(String itemId);

    @Column(name="qty")
    public int getQuantity();
    public void setQuantity(int quantity);

    @MappingColumn(namedQuery = ProductDAOImpl.GET_PRODUCT,
            params = {@ParameterMap(name="id",value = "$this.getProductId()")})
    public Product getProduct();
//    public void setProduct(Product product);

    @Column(name="productid")
    public String getProductId();
    public void setProductId(String productId);

    @Column(name="supplier")
    public int getSupplierId();
    public void setSupplierId(int supplierId);

    @Column(name="listprice")
    public double getListPrice();
    public void setListPrice(double listPrice);

    @Column(name="unitcost")
    public double getUnitCost();
    public void setUnitCost(double unitCost);

    @Column(name="status")
    public String getStatus();
    public void setStatus(String status);

    @Column(name = "attr1")
    public String getAttribute1();
    public void setAttribute1(String attribute1);

    @Column(name = "attr2")
    public String getAttribute2();
    public void setAttribute2(String attribute2);

    @Column(name = "attr3")
    public String getAttribute3();
    public void setAttribute3(String attribute3);

    @Column(name = "attr4")
    public String getAttribute4();
    public void setAttribute4(String attribute4);

    @Column(name = "attr5")
    public String getAttribute5();
    public void setAttribute5(String attribute5);

}
