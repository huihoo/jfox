package net.sourceforge.jfox.framework.example;

import javax.persistence.Column;

import net.sourceforge.jfox.entity.annotation.EntityHelper;
import net.sourceforge.jfox.entity.EntityObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@EntityHelper(Address.Helper.class)
public interface Address extends EntityObject {
    
    @Column(name="ADR_ID")
    long getId();
    void setId(long id);

    @Column(name="ADR_ACC_ID")
    long getAccountId();
    void setAccountId(long accountId);

    @Column(name="ADR_DESCRIPTION")
    String getDescription();
    void setDescription(String description);

    @Column(name="ADR_STREET")
    String getStreet();
    void setStreet(String street);

    @Column(name="ADR_CITY")
    String getCity();
    void setCity(String city);

    @Column(name="ADR_PROVINCE")
    String getProvince();
    void setProvince(String province);

    @Column(name="ADR_POSTAL_CODE")
    String getPostalCode();
    void setPostalCode(String postalCode);

    public static class Helper {
        public String getIdNamePair(Address adderss) {
            return adderss.getId() + "";
        }
    }
}
