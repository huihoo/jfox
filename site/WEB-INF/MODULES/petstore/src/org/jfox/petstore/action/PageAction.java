package org.jfox.petstore.action;

import org.jfox.framework.annotation.Service;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "page")
public class PageAction extends ActionSupport {

    /**
     * index page
     * 
     * @param invocationContext invocationContext
     * @throws Exception exception
     */
    @ActionMethod(successView = "index.vhtml")
    public void doGetIndex(InvocationContext invocationContext) throws Exception {
        PageContext pageContext = invocationContext.getPageContext();
        SessionContext sessionContext = invocationContext.getSessionContext();
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
