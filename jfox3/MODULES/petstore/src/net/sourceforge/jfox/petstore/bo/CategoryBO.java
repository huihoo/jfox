package net.sourceforge.jfox.petstore.bo;

import java.util.List;

import net.sourceforge.jfox.petstore.entity.Category;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */

public interface CategoryBO {

    Category getCategory(String categoryId);

    List<Category> getCategoryList();
}
