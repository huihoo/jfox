package org.jfox.petstore.bo;

import java.util.List;
import java.util.Collections;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jfox.petstore.dao.CategoryDAO;
import org.jfox.petstore.entity.Category;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless
public class CategoryBOImpl implements CategoryBO{

    @EJB
    CategoryDAO categoryDAO;

    public Category getCategory(String categoryId) {
        try {
            return categoryDAO.getCategory(categoryId);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Category> getCategoryList() {
        try {
            return categoryDAO.getCategoryList();
        }
        catch(Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
