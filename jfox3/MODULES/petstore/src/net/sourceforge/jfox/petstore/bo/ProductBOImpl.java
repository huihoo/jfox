package net.sourceforge.jfox.petstore.bo;

import java.sql.SQLException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.ejb.Stateless;

import net.sourceforge.jfox.petstore.dao.ProductDAO;
import net.sourceforge.jfox.petstore.entity.Product;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Stateless
@Local
public class ProductBOImpl implements ProductBO{

    @EJB
    ProductDAO productDAO;

    public List<Product> getProductsByCategory(String categoryId) {
        try {
            return productDAO.getProductListByCategory(categoryId);
        }
        catch(SQLException e) {
            throw new EJBException(e);
        }
    }


    public Product getProduct(String productId) {
        try {
            return productDAO.getProduct(productId);
        }
        catch(SQLException e) {
            throw new EJBException(e);
        }
    }

    public List<Product> searchProductList(String[] keywords) {
        try {
            String[] sqlKeyWords = new String[keywords.length];
            for(int i=0; i< keywords.length; i++){
                String sqlKW = "%" + keywords[i] + "%";
                sqlKeyWords[i] = sqlKW;
            }
            return productDAO.searchProductList(sqlKeyWords);
        }
        catch(SQLException e) {
            throw new EJBException(e);
        }
    }

    public static void main(String[] args) {

    }
}
