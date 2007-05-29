/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import org.jfox.petstore.entity.Product;

public interface ProductDAO {

    List<Product> getProductListByCategory(String categoryId) throws SQLException;

    List<Product> searchProductList(String[] keywords) throws SQLException;

    Product getProduct(String productId) throws SQLException;

}
