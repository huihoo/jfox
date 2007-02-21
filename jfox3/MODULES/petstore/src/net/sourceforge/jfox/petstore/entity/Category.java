package net.sourceforge.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public interface Category extends Serializable {

    @Column(name = "catid")
    public String getCategoryId();
    public void setCategoryId(String categoryId);

    @Column(name = "name")
    public String getName();
    public void setName(String name);

    @Column(name = "descn")
    public String getDescription();
    public void setDescription(String description);

}
