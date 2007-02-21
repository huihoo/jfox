package net.sourceforge.jfox.petstore.action;

import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.SessionContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
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
