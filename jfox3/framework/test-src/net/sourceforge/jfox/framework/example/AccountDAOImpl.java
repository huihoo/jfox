package net.sourceforge.jfox.framework.example;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import javax.ejb.Stateless;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sourceforge.jfox.entity.EntityObject;
import net.sourceforge.jfox.entity.EntityManagerExt;
import net.sourceforge.jfox.entity.QueryExt;
import net.sourceforge.jfox.entity.dao.DAOSupport;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(name = AccountDAOImpl.Get_ACCOUNT_BY_ID,
                query = "select * from account where ACC_ID=$id",
                resultClass = Account.class
        ),
        @NamedNativeQuery(name = AccountDAOImpl.Get_ALL_ACCOUNTS,
                query = "select * from account",
                resultClass = Account.class
        ),
        @NamedNativeQuery(name = AccountDAOImpl.Create_ACCOUNT_BY_ID,
                query = "insert into account values($account.getId(),$account.getFirstName(),$account.getLastName(), $account.getMail())"
        )
                }
)
@Stateless
public class AccountDAOImpl extends DAOSupport implements AccountDAO {

    public static final String Get_ACCOUNT_BY_ID = "AccountDAO.getAccountById";
    public static final String Get_ALL_ACCOUNTS = "AccountDAO.getAllAccounts";
    public static final String Create_ACCOUNT_BY_ID = "AccountDAO.createAccount";

    @PersistenceContext(unitName = "JPetstoreMysqlDS")
    EntityManager em;

    protected EntityManagerExt getEntityManager() {
        return (EntityManagerExt)em;
    }

    public Account getAccountById(long id) throws SQLException {
        Query query = createNamedNativeQuery(Get_ACCOUNT_BY_ID).setParameter("id", id);
        return (Account)query.getSingleResult();
    }

    public Account getAccountByIdSQL(long id) throws SQLException {
        Query query = createNativeQuery("select * from ACCOUNT where ACC_ID=$p1", Account.class).setParameter("id", id);
        return (Account)query.getSingleResult();
    }

    public EntityObject getAccountByIdSQLEntityObject(long id) throws SQLException {
        Query query = createNativeQuery("select * from ACCOUNT where ACC_ID=$p1", EntityObject.class).setParameter("id", id);
        return (EntityObject)query.getSingleResult();
    }

    public List<Account> getAllAccounts() throws SQLException {
        QueryExt query = createNamedNativeQuery(Get_ALL_ACCOUNTS);
        return (List<Account>)query.getResultList();
    }

    public Account createAccount(String name, String lastname, String mail) throws SQLException {
        Account account = newEntityObject(Account.class);
        account.setId(new Random().nextInt());
        account.setFirstName(name);
        account.setLastName(lastname);
        account.setMail(mail);
        Query query = createNamedNativeQuery(Create_ACCOUNT_BY_ID).setParameter("account", account);
        int result = query.executeUpdate();
        return account;
    }

}
