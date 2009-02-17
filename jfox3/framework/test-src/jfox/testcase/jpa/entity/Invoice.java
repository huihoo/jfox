package jfox.testcase.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-2-17 11:41:20
 */
@Entity
public class Invoice implements Serializable {

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "CustomerID")
    private int customerId;

    @Column(name = "Total")
    private float total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Invoice{ID="+getId() + ", CustomerId=" + getCustomerId() + ", Total=" + getTotal() + "}";
    }
}
