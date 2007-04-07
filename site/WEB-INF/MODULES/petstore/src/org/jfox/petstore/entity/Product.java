package org.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public interface Product extends Serializable {

    @Column(name = "productid")
    public String getProductId();
    public void setProductId(String productId);

    @Column(name = "category")
    public String getCategoryId();
    public void setCategoryId(String categoryId);

    @Column(name = "name")
    public String getName();
    public void setName(String name);

    @Column(name = "descn")
    public String getDescription();
    public void setDescription(String description);

}
