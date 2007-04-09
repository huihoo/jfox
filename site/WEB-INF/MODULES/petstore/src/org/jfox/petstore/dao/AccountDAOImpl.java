package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedNativeQueries;
import javax.persistence.EntityManager;
import javax.persistence.QueryHint;

import org.jfox.petstore.entity.Account;
import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.dao.DAOSupport;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(
                name = AccountDAOImpl.GET_ACCOUNT_BY_USERNAME,
                query = "select " +
                        "signon.username as userid," +
                        "account.email as email, " +
                        "account.firstname as firstname," +
                        "account.lastname as lastname," +
                        "account.status as status," +
                        "account.addr1 as addr1," +
                        "account.addr2 as addr2," +
                        "account.city as city," +
                        "account.state as state," +
                        "account.zip as zip," +
                        "account.country as country," +
                        "account.phone as phone," +
                        "profile.langpref as langpref," +
                        "profile.favcategory as favcategory," +
                        "profile.mylistopt as mylistopt," +
                        "profile.banneropt as banneropt," +
                        "bannerdata.bannername as bannername" +
                        " from account, profile, signon, bannerdata" +
                        " where account.userid = $username" +
                        " and signon.username = account.userid" +
                        " and profile.userid = account.userid" +
                        " and profile.favcategory = bannerdata.favcategory",
                resultClass = Account.class,
                hints = {
                        @QueryHint(name = "cache.default.partition", value = "account")
                        }
        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.GET_ACCOUNT_BY_USERNAME_AND_PASSWORD,
                query = "select " +
                        "signon.username as userid," +
                        "account.email," +
                        "account.firstname," +
                        "account.lastname," +
                        "account.status," +
                        "account.addr1," +
                        "account.addr2," +
                        "account.city," +
                        "account.state," +
                        "account.zip," +
                        "account.country," +
                        "account.phone," +
                        "profile.langpref," +
                        "profile.favcategory," +
                        "profile.mylistopt," +
                        "profile.banneropt," +
                        "bannerdata.bannername" +
                        " from account, profile, signon, bannerdata" +
                        " where account.userid = $username" +
                        " and signon.password = $password" +
                        " and signon.username = account.userid" +
                        " and profile.userid = account.userid" +
                        " and profile.favcategory = bannerdata.favcategory",
                resultClass = Account.class,
                hints = {
                        @QueryHint(name = "cache.default.partition", value = "account")
                        }

        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.GET_USERNAME_LIST,
                query = "select username as userid from signon",
                resultClass = String.class,
                hints = {
                        @QueryHint(name = "cache.default.partition", value = "account")
                        }

        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.UPDATE_ACCOUNT,
                query = "update account set " +
                        "email = $account.getEmail(), " +
                        "firstname = $account.getFirstName(), " +
                        "lastname = $account.getLastName(), " +
                        "status = $account.getStatus(), " +
                        "addr1 = $account.getAddress1(), " +
                        "addr2 = $account.getAddress2(), " +
                        "city = $account.getCity(), " +
                        "state = $account.getState(), " +
                        "zip = $account.getZip(), " +
                        "country = $account.getCountry(), " +
                        "phone = $account.getPhone() " +
                        "where " +
                        "userid = $account.getUsername()",
                hints = {
                        @QueryHint(name = "cache.default.partition", value = "account")
                        }

        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.INSERT_ACCOUNT,
                query = "insert into account (" +
                        "email, " +
                        "firstname, " +
                        "lastname, " +
                        "status, " +
                        "addr1, " +
                        "addr2, " +
                        "city, " +
                        "state, " +
                        "zip, " +
                        "country, " +
                        "phone, " +
                        "userid" +
                        ") values (" +
                        "$account.getEmail(), " +
                        "$account.getFirstName(), " +
                        "$account.getLastName(), " +
                        "$account.getStatus(), " +
                        "$account.getAddress1(), " +
                        "$account.getAddress2(), " +
                        "$account.getCity(), " +
                        "$account.getState(), " +
                        "$account.getZip(), " +
                        "$account.getCountry(), " +
                        "$account.getPhone(), " +
                        "$account.getUsername())",
                hints = {
                        @QueryHint(name = "cache.default.partition", value = "account")
                        }

        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.UPDATE_PROFILE,
                query = "update profile set langpref = $account.getLanguagePreference(), favcategory = $account.getFavouriteCategoryId(), mylistopt = $account.getListOption(), banneropt = $account.getBannerOption() where userid = $account.getUsername()"
        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.INSERT_PROFILE,
                query = "insert into profile (" +
                        "langpref, " +
                        "favcategory, " +
                        "mylistopt, " +
                        "banneropt, " +
                        "userid" +
                        ") values (" +
                        "$account.getLanguagePreference(), " +
                        "$account.getFavouriteCategoryId(), " +
                        "$account.getListOption(), " +
                        "$account.getBannerOption(), " +
                        "$account.getUsername())"
        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.UPDATE_SIGNON,
                query = "update signon set password = $password where username = $username"
        ),

        @NamedNativeQuery(
                name = AccountDAOImpl.INSERT_SIGNON,
                query = "insert into signon (password,username) values ($account.getPassword(),$account.getUsername())"
        )

                }
)
@Stateless
@Local
@SuppressWarnings("unchecked")
public class AccountDAOImpl extends DAOSupport implements AccountDAO {
    public static final String GET_ACCOUNT_BY_USERNAME = "getAccountByUsername";
    public static final String GET_ACCOUNT_BY_USERNAME_AND_PASSWORD = "getAccountByUsernameAndPassword";
    public static final String GET_USERNAME_LIST = "getUsernameList";
    public static final String UPDATE_ACCOUNT = "updateAccount";
    public static final String INSERT_ACCOUNT = "insertAccount";
    public static final String UPDATE_PROFILE = "updateProfile";
    public static final String INSERT_PROFILE = "insertProfile";
    public static final String UPDATE_SIGNON = "updateSignon";
    public static final String INSERT_SIGNON = "insertSignon";

    /**
     * 用来进行数据库操作
     */
    @PersistenceContext(unitName = "JPetstoreMysqlDS")
    private EntityManagerExt em = null;

    /**
     * 返回 EntityManager，默认注入的是 default
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    public Account getAccount(String username) throws SQLException {
        Query query = createNamedNativeQuery(GET_ACCOUNT_BY_USERNAME).setParameter("username", username);
        return (Account)query.getSingleResult();
    }

    public Account getAccount(String username, String password)
            throws SQLException {
        Query query = createNamedNativeQuery(GET_ACCOUNT_BY_USERNAME_AND_PASSWORD)
                            .setParameter("username",username)
                            .setParameter("password",password);
        return (Account)query.getSingleResult();
    }

    public List<String> getUsernameList() throws SQLException {
        Query query = createNamedNativeQuery(GET_USERNAME_LIST);
        return (List<String>)query.getResultList();
    }

    public void insertAccount(Account account) throws SQLException {
//        getEntityManager().getTransaction().begin();
        createNamedNativeQuery(INSERT_ACCOUNT).setParameter("account", account).executeUpdate();
        createNamedNativeQuery(INSERT_SIGNON).setParameter("account", account).executeUpdate();
        createNamedNativeQuery(INSERT_PROFILE).setParameter("account", account).executeUpdate();
//        em.getTransaction().commit();
    }

    public void updateAccount(Account account) throws SQLException {
        createNamedNativeQuery(UPDATE_ACCOUNT).setParameter("account", account).executeUpdate();
        createNamedNativeQuery(UPDATE_PROFILE).setParameter("account", account).executeUpdate();
        createNamedNativeQuery(UPDATE_SIGNON).setParameter("username", account.getUsername()).setParameter("password",account.getPassword()).executeUpdate();
    }

}
