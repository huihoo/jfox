package jfox.test.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.jfox.entity.annotation.MappingColumn;
import org.jfox.entity.annotation.ParameterMap;
import jfox.test.ejbcomponent.dao.AddressDAO;

/**
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
public class Account {

    @Column(name="ACC_ID")
    long id;

    @Column(name="ACC_FIRST_NAME")
    String firstName;

    @Column(name="ACC_LAST_NAME")
    String lastName;

    @Column(name="ACC_EMAIL")
    String mail;

    @MappingColumn(namedQuery = AddressDAO.GET_ADDRESS_BY_ACCOUNT_ID, params = {@ParameterMap(name = "accountId",value="$this.getId()")})
    Address address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String toString() {
        return "Account{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mail='" + mail + '\'' +
                ", address=" + address +
                '}';
    }
}
