package jfox.test.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.jfox.entity.annotation.MappedColumn;
import org.jfox.entity.annotation.ParameterMap;
import jfox.test.ejbcomponent.dao.AddressDAO;

/**
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
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

    @MappedColumn(namedQuery = AddressDAO.GET_ADDRESS_BY_ACCOUNT_ID, params = {@ParameterMap(name = "accountId",value="$this.getId()")})
    Address getAddress();
}
