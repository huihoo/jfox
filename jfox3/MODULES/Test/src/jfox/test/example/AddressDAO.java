package jfox.test.example;

import javax.ejb.Stateless;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedNativeQueries;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import net.sourceforge.jfox.entity.dao.DAOSupport;
import net.sourceforge.jfox.entity.EntityManagerExt;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(name = "AddressDAO.getAddressById",
                query = "select * from address where id = $id",
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

    protected EntityManagerExt getEntityManager() {
        return (EntityManagerExt)em;
    }
    public static void main(String[] args) {

    }
}
