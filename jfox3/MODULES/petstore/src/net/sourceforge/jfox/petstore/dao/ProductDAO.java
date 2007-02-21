package net.sourceforge.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import net.sourceforge.jfox.petstore.entity.Product;

public interface ProductDAO {

    List<Product> getProductListByCategory(String categoryId) throws SQLException;

    List<Product> searchProductList(String[] keywords) throws SQLException;

    Product getProduct(String productId) throws SQLException;

}
