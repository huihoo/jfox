/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.action;

import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.ParameterObject;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.petstore.bo.ItemBO;
import org.jfox.petstore.entity.Item;

import javax.ejb.EJB;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name ="item")
public class ItemAction extends ActionSupport {

    @EJB
    ItemBO itemBO;

    @ActionMethod(name="view", successView = "Item.vhtml", parameterClass = ItemParameterObject.class, httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetView(ActionContext actionContext) throws Exception {
        ItemParameterObject invocation = (ItemParameterObject)actionContext.getParameterObject();
        Item item = itemBO.getItem(invocation.getItemId());

        PageContext pageContext = actionContext.getPageContext();
        
        pageContext.setAttribute("item",item);
        
    }

    public static class ItemParameterObject extends ParameterObject {
        private String itemId;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }
}
