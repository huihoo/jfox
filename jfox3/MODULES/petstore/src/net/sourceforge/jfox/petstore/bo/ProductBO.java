package net.sourceforge.jfox.petstore.bo;

import java.util.List;

import net.sourceforge.jfox.petstore.entity.Product;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface ProductBO {

    List<Product> getProductsByCategory(String categoryId);

    List<Product> searchProductList(String[] keywords);

    Product getProduct(String productId);
}
