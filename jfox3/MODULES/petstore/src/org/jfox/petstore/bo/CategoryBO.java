package org.jfox.petstore.bo;

import java.util.List;

import org.jfox.petstore.entity.Category;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public interface CategoryBO {

    Category getCategory(String categoryId);

    List<Category> getCategoryList();
}
