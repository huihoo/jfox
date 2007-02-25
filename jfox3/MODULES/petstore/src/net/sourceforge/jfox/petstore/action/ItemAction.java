package net.sourceforge.jfox.petstore.action;

import javax.ejb.EJB;

import net.sourceforge.jfox.petstore.bo.ItemBO;
import net.sourceforge.jfox.petstore.entity.Item;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id="item", active = true)
public class ItemAction extends ActionSupport {

    @EJB
    ItemBO itemBO;

    @ActionMethod(successView = "item.vhtml", invocationClass = ItemInvocation.class)
    public void doGetView(InvocationContext invocationContext) throws Exception {
        ItemInvocation invocation = (ItemInvocation)invocationContext.getInvocation();
        Item item = itemBO.getItem(invocation.getItemId());

        PageContext pageContext = invocationContext.getPageContext();
        
        pageContext.setAttribute("item",item);
        
    }

    @ActionMethod(successView = "item.vm", invocationClass = ItemInvocation.class)
    public void doPostView(InvocationContext invocationContext) throws Exception{

    }


    public static class ItemInvocation extends Invocation {
        private String itemId;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }
}
