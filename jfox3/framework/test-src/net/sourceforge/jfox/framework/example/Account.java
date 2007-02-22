package net.sourceforge.jfox.framework.example;

import javax.persistence.Entity;
import javax.persistence.Column;

import net.sourceforge.jfox.entity.annotation.MappedColumn;
import net.sourceforge.jfox.entity.annotation.EntityHelper;
import net.sourceforge.jfox.entity.annotation.ParameterMap;

/**
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Entity
@EntityHelper(Account.Helper.class)
public interface Account {

    @Column(name="ACC_ID")
    long getId();
    void setId(long id);

    @Column(name="ACC_FIRST_NAME")
    String getFirstName();
    void setFirstName(String name);

    @Column(name="ACC_LAST_NAME")
    String getLastName();
    void setLastName(String name);

    @Column(name="ACC_EMAIL")
    String getMail();
    void setMail(String mail);

/*
    @Column(name="ACC_ADDRESS_ID")
    long getAddressId();
    void setAddressId(long addressId);
*/

    @MappedColumn(namedQuery = AddressDAO.GET_ADDRESS_BY_ACCOUNT_ID, params = {@ParameterMap(name = "id",value="$this.getId()")})
    Address getAddress();

    public static class Helper {
        public String toString(Account account) {
            return account.getId() + ":" + account.getFirstName() + ":" + account.getLastName() + ":" + account.getMail();
        }
    }
}
