/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.action;

import javax.ejb.EJB;

import org.jfox.petstore.bo.ItemBO;
import org.jfox.petstore.entity.Item;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.annotation.ActionMethod;

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
