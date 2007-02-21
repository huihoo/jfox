package net.sourceforge.jfox.petstore.bo;

import java.util.List;
import java.util.Collections;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.sourceforge.jfox.petstore.dao.CategoryDAO;
import net.sourceforge.jfox.petstore.entity.Category;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
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
