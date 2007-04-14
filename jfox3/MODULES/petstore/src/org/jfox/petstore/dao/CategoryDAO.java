package org.jfox.petstore.dao;

import java.sql.SQLException;
import java.util.List;

import org.jfox.petstore.entity.Category;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface CategoryDAO {

    List<Category> getCategoryList() throws SQLException;

    Category getCategory(String categoryId) throws SQLException;

}
