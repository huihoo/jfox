package net.sourceforge.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.ejb.Local;
import javax.ejb.Stateless;

import net.sourceforge.jfox.entity.dao.DAOSupport;
import net.sourceforge.jfox.entity.EntityManagerExt;
import net.sourceforge.jfox.petstore.entity.Category;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@NamedNativeQueries(
        {
        @NamedNativeQuery(
                name = CategoryDAOImpl.GET_CATEGORY,
                query = "select catid, name, descn from category where catid = $id",
                resultClass = Category.class
        ),
        @NamedNativeQuery(
                name = CategoryDAOImpl.GET_CATEGORY_LIST,
                query = "select catid, name, descn from category",
                resultClass = Category.class
        )
                }
)
@Stateless
@Local
public class CategoryDAOImpl extends DAOSupport implements CategoryDAO{

    public static final String GET_CATEGORY = "getCategory";
    public static final String GET_CATEGORY_LIST = "getCategoryList";

    @PersistenceContext(unitName = "JPetstoreMysqlDS")
    EntityManager em;

    protected EntityManagerExt getEntityManager() {
        return (EntityManagerExt)em;
    }

    public Category getCategory(String categoryId) throws SQLException {
        return (Category)createNamedNativeQuery(GET_CATEGORY).setParameter("id",categoryId).getSingleResult();
    }

    public List<Category> getCategoryList() throws SQLException {
        return (List<Category>)createNamedNativeQuery(GET_CATEGORY_LIST).getResultList();
    }

    public static void main(String[] args) {

    }
}
