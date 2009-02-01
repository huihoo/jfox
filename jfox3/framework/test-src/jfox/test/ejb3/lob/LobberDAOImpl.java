/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.lob;

import org.jfox.entity.support.dao.DAOSupport;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless(name = "lob.LobberDAO")
@NamedNativeQueries(
        {
        @NamedNativeQuery(
                name = LobberDAOImpl.INSERT_LOBBER,
                query = "insert into lobber(id,blobby,clobby) values($lobber.getId(),$lobber.getBlobby(),$lobber.getClobby())"
        ),
        @NamedNativeQuery(
                name = LobberDAOImpl.GET_LOBBER,
                query = "select * from lobber where id=$id",
                resultClass = Lobber.class
        ),
        @NamedNativeQuery(
                name = LobberDAOImpl.UPDATE_LOBBER,
                query = "update lobber set blobby=$lobber.getBlobby(), clobby=$lobber.getClobby() where id=$lobber.getId()"
        ),
        @NamedNativeQuery(
                name = LobberDAOImpl.DELETE_LOBBER,
                query = "delete from lobber where id=$id"
        )
                })
public class LobberDAOImpl extends DAOSupport implements LobberDAO {
    public static final String INSERT_LOBBER = "INSERT_LOBBER";
    public static final String GET_LOBBER = "GET_LOBBER";
    public static final String UPDATE_LOBBER = "UPDATE_LOBBER";
    public static final String DELETE_LOBBER = "DELETE_LOBBER";

    @PersistenceContext(unitName = "default")
    EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public void insertLobber(Lobber lobber) {
        createNamedNativeQuery(INSERT_LOBBER).setParameter("lobber",lobber).executeUpdate();
    }

    public Lobber getLobber(int id){
        return (Lobber)createNamedNativeQuery(GET_LOBBER).setParameter("id",id).getSingleResult();
    }

    public void updateLobber(Lobber lobber) {
        createNamedNativeQuery(UPDATE_LOBBER).setParameter("lobber",lobber).executeUpdate();
    }

    public void deleteLobber(int id) {
        createNamedNativeQuery(DELETE_LOBBER).setParameter("id",id).executeUpdate();
    }

}
