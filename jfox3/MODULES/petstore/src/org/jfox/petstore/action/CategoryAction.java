/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.action;

import java.util.List;
import javax.ejb.EJB;

import org.jfox.petstore.bo.AccountBO;
import org.jfox.petstore.bo.CategoryBO;
import org.jfox.petstore.bo.ProductBO;
import org.jfox.petstore.entity.Product;
import org.jfox.petstore.entity.Category;
import org.jfox.mvc.util.PagedList;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "category")
public class CategoryAction extends ActionSupport {

    @EJB
    AccountBO accountBO;

    @EJB(name = "CategoryBOImpl")
    CategoryBO categoryBO;

    @EJB
    ProductBO productBO;
    
    public void postInject() {
        super.postInject();
    }

    /**
     * index page
     *
     * @param invocationContext invocationContext
     * @throws Exception exception
     */
    @ActionMethod(successView = "Category.vhtml", invocationClass = CategoryInvocation.class)
    public void doGetView(InvocationContext invocationContext) throws Exception {
        CategoryInvocation invocation = (CategoryInvocation)invocationContext.getInvocation();
        Category category = categoryBO.getCategory(invocation.getCategoryId());

        PageContext pageContext = invocationContext.getPageContext();
        SessionContext sessionContext = invocationContext.getSessionContext();

        PagedList<Product> productPagedList;

        //product list cached by JPA Cache
//        if (!sessionContext.containsAttribute("ProductPageList")) {
            List<Product> products = productBO.getProductsByCategory(invocation.getCategoryId());
            productPagedList = new PagedList<Product>(products, 4);
            sessionContext.setAttribute("ProductPageList", productPagedList);
//        }
//        else {
//            productPagedList = (PagedList<Product>)sessionContext.getAttribute("ProductPageList");
//        }
        for(int i=0; i< invocation.getPage(); i++){
            productPagedList.nextPage();
        }

        int nextPage = invocation.getPage();
        if(!productPagedList.isLastPage()){
            nextPage++;
        }

        int previousPage = invocation.getPage();
        if(!productPagedList.isFirstPage()){
            previousPage--;
        }

/*
        if ("next".equals(invocation.getPage())) {
            productPagedList.nextPage();
        }
        else if ("previous".equals(invocation.getPage())) {
            productPagedList.previousPage();
        }
*/

//        Account account = accountBO.getAccount();
//        account.setFirstName("Yang Yong");
//        sessionContext.setAttribute("account", account);

        pageContext.setAttribute("account", sessionContext.getAttribute("account"));
        pageContext.setAttribute("category", category);
        pageContext.setAttribute("categoryId", invocation.getCategoryId());
        pageContext.setAttribute("pageList", productPagedList);
        pageContext.setAttribute("previousPage", previousPage);
        pageContext.setAttribute("nextPage", nextPage);

    }

    public static class CategoryInvocation extends Invocation {

        private String categoryId = "BIRDS";

        private int page = 0;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }


        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }

    public static void main(String[] args) {

    }
}
