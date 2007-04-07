package jfox.test.ejbcomponent.dao;

import javax.ejb.Stateless;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedNativeQueries;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import org.jfox.entity.dao.DAOSupport;
import org.jfox.entity.EntityManagerExt;
import jfox.test.jpa.Address;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(name = "AddressDAO.getAddressById",
                query = "select * from address where ADR_ID = $id",
                resultClass = Address.class),
        @NamedNativeQuery(name = AddressDAO.GET_ADDRESS_BY_ACCOUNT_ID,
                query = "select * from address where ADR_ACC_ID = $p1",
                resultClass = Address.class),
        @NamedNativeQuery(name = "AddressDAO.getAllAddress",
                query = "select * from address",
                resultClass = Address.class)
                }
)
@Stateless
public class AddressDAO extends DAOSupport {

    public static final String GET_ADDRESS_BY_ACCOUNT_ID = "AddressDAO.getAddressByAccountId";

    @PersistenceContext(unitName = "JPetstoreMysqlDS")
    EntityManager em;

    protected EntityManager getEntityManager() {
        return (EntityManagerExt)em;
    }
    public static void main(String[] args) {

    }
}
