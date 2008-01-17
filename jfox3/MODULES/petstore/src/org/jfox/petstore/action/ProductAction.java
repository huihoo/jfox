/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.action;

import java.util.List;
import javax.ejb.EJB;

import org.jfox.framework.annotation.Service;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.util.PagedList;
import org.jfox.petstore.bo.ItemBO;
import org.jfox.petstore.bo.ProductBO;
import org.jfox.petstore.entity.Item;
import org.jfox.petstore.entity.Product;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "product")
public class ProductAction extends ActionSupport {

    @EJB
    ItemBO itemBO;

    @EJB
    ProductBO productBO;

    @ActionMethod(name="view", successView = "Product.vhtml", invocationClass = ProductInvoation.class, httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetView(ActionContext actionContext) {
        ProductInvoation invocation = (ProductInvoation)actionContext.getInvocation();

        Product product = productBO.getProduct(invocation.getProductId());

        List<Item> items = itemBO.getItemListByProduct(invocation.getProductId());
        PagedList<Item> itemPageList = new PagedList<Item>(items, 4);
        for (int i = 0; i < invocation.getPage(); i++) {
            itemPageList.nextPage();
        }

        int nextPage = invocation.getPage();
        if (!itemPageList.isLastPage()) {
            nextPage++;
        }

        int previousPage = invocation.getPage();
        if (!itemPageList.isFirstPage()) {
            previousPage--;
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("product", product);
        pageContext.setAttribute("productId", invocation.getProductId());
        pageContext.setAttribute("itemPageList", itemPageList);
        pageContext.setAttribute("previousPage", previousPage);
        pageContext.setAttribute("nextPage", nextPage);

    }

    @ActionMethod(name="search", successView = "SearchProducts.vhtml", invocationClass = SearchProductInvocation.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostSearch(ActionContext actionContext) {
        SearchProductInvocation invocation = (SearchProductInvocation)actionContext.getInvocation();
        String keyword = invocation.getKeyword();
        String[] keywords = keyword.split(" ");
        List<Product> products = productBO.searchProductList(keywords);
        PagedList<Product> productPagedList = new PagedList<Product>(products,4);

        for (int i = 0; i < invocation.getPage(); i++) {
            productPagedList.nextPage();
        }

        int nextPage = invocation.getPage();
        if (!productPagedList.isLastPage()) {
            nextPage++;
        }

        int previousPage = invocation.getPage();
        if (!productPagedList.isFirstPage()) {
            previousPage--;
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("productPageList", productPagedList);
        pageContext.setAttribute("previousPage", previousPage);
        pageContext.setAttribute("nextPage", nextPage);
    }

    @ActionMethod(name="search", successView = "SearchProducts.vhtml", invocationClass = SearchProductInvocation.class, httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetSearch(ActionContext actionContext) {
        doPostSearch(actionContext);
    }

    public static class ProductInvoation extends Invocation {
        private String productId;

        private int page = 0;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }

    public static class SearchProductInvocation extends Invocation {

        private String keyword = "";

        private int page = 0;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
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
