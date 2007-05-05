package jfox.test.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
public class Address {
    
    @Column(name="ADR_ID")
    long id;

    @Column(name="ADR_ACC_ID")
    long accountId;

    @Column(name="ADR_DESCRIPTION")
    String description;

    @Column(name="ADR_STREET")
    String street;

    @Column(name="ADR_CITY")
    String city;

    @Column(name="ADR_PROVINCE")
    String province;

    @Column(name="ADR_POSTAL_CODE")
    String postalCode;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
