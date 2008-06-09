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
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name = "page")
public class PageAction extends ActionSupport {

    /**
     * index page
     * 
     * @param actionContext invocationContext
     * @throws Exception exception
     */
    @ActionMethod(name="index", successView = "index.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetIndex(ActionContext actionContext) throws Exception {
        PageContext pageContext = actionContext.getPageContext();
        SessionContext sessionContext = actionContext.getSessionContext();
/*
        Account account = DAOSupport.newEntityObject(Account.class);
        account.setFirstName("Yang Yong");
        sessionContext.setAttribute("account", account);
*/
        pageContext.setAttribute("account", sessionContext.getAttribute("account"));
    }

    public static void main(String[] args) {

    }
}
