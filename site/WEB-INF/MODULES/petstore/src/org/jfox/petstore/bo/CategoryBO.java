/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
