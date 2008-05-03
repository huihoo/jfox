/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejbcomponent.dao;

import jfox.test.jpa.Address;
import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.dao.DAOSupport;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(name = "AddressDAO.getAddressById",
                query = "select * from address where ADR_ID = $id",
                resultClass = Address.class),
        @NamedNativeQuery(name = AddressDAO.GET_ADDRESS_BY_ACCOUNT_ID,
                query = "select * from address where ADR_ACC_ID = $accountId",
                resultClass = Address.class),
        @NamedNativeQuery(name = "AddressDAO.getAllAddress",
                query = "select * from address",
                resultClass = Address.class)
                }
)
@Stateless
public class AddressDAO extends DAOSupport {

    public static final String GET_ADDRESS_BY_ACCOUNT_ID = "AddressDAO.getAddressByAccountId";

    @PersistenceContext(unitName = "DefaultMysqlDS")
    EntityManager em;

    protected EntityManager getEntityManager() {
        return (EntityManagerExt)em;
    }
    public static void main(String[] args) {

    }
}
