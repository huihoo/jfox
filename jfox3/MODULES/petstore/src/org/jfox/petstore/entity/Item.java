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
public class Item implements Serializable {

    @Column(name="itemid")
    String itemId;

    @Column(name="qty")
    int quantity;

    @MappingColumn(namedQuery = ProductDAOImpl.GET_PRODUCT,
            params = {@ParameterMap(name="id",value = "$this.getProductId()")})
    Product product;

    @Column(name="productid")
    String productId;

    @Column(name="supplier")
    int supplierId;

    @Column(name="listprice")
    double listPrice;

    @Column(name="unitcost")
    double unitCost;

    @Column(name="status")
    String status;

    @Column(name = "attr1")
    String attribute1;

    @Column(name = "attr2")
    String attribute2;

    @Column(name = "attr3")
    String attribute3;

    @Column(name = "attr4")
    String attribute4;

    @Column(name = "attr5")
    String attribute5;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public String getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    public String getAttribute3() {
        return attribute3;
    }

    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
    }

    public String getAttribute4() {
        return attribute4;
    }

    public void setAttribute4(String attribute4) {
        this.attribute4 = attribute4;
    }

    public String getAttribute5() {
        return attribute5;
    }

    public void setAttribute5(String attribute5) {
        this.attribute5 = attribute5;
    }
}
