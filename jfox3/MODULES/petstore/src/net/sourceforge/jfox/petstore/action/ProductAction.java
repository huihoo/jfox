package net.sourceforge.jfox.petstore.action;

import java.util.List;
import javax.ejb.EJB;

import net.sourceforge.jfox.petstore.bo.ItemBO;
import net.sourceforge.jfox.petstore.bo.ProductBO;
import net.sourceforge.jfox.petstore.entity.Item;
import net.sourceforge.jfox.petstore.entity.Product;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.util.PagedList;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Service(id = "product")
public class ProductAction extends ActionSupport {

    @EJB
    ItemBO itemBO;

    @EJB
    ProductBO productBO;

    @ActionMethod(successView = "Product.vhtml", invocationClass = ProductInvoation.class)
    public void doGetView(InvocationContext invocationContext) {
        ProductInvoation invocation = (ProductInvoation)invocationContext.getInvocation();

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

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("product", product);
        pageContext.setAttribute("productId", invocation.getProductId());
        pageContext.setAttribute("itemPageList", itemPageList);
        pageContext.setAttribute("previousPage", previousPage);
        pageContext.setAttribute("nextPage", nextPage);

    }

    @ActionMethod(successView = "SearchProducts.vhtml", invocationClass = SearchProductInvocation.class)
    public void doPostSearch(InvocationContext invocationContext) {
        SearchProductInvocation invocation = (SearchProductInvocation)invocationContext.getInvocation();
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

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("productPageList", productPagedList);
        pageContext.setAttribute("previousPage", previousPage);
        pageContext.setAttribute("nextPage", nextPage);
    }

    @ActionMethod(successView = "SearchProducts.vhtml", invocationClass = SearchProductInvocation.class)
    public void doGetSearch(InvocationContext invocationContext) {
        doPostSearch(invocationContext);
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
