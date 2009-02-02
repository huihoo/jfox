/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
@Table(name = "ACCOUNT")
public class Account implements Serializable {

    public static final String DB_COLUMN_ID = "ACC_ID";

    @Column(name="ACC_ID")
    @Id
    long id;

    @Column(name="ACC_FIRST_NAME")
    String firstName;

    @Column(name="ACC_LAST_NAME")
    String lastName;

    @Column(name="ACC_EMAIL")
    String mail;

    @JoinColumn(columnDefinition = "select * from address where ADR_ACC_ID = $ACC_ID")
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
