package net.sourceforge.jfox.manager;

import java.util.ArrayList;

import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.SessionContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id="carts")
public class CartsAction extends ActionSupport {

    @ActionMethod(successView = "template/carts.fhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "template/carts.fhtml", invocationClass = CartInvocation.class)
    public void doPostSubmit(InvocationContext invocationContext) throws Exception {
        CartInvocation invocation = (CartInvocation)invocationContext.getInvocation();
        SessionContext sessionContext = invocationContext.getSessionContext();
        ArrayList<String> carts = (ArrayList<String>)sessionContext.getAttribute("carts");
        if(carts == null) {
            carts = new ArrayList<String>();
            sessionContext.setAttribute("carts", carts);
        }

        if(invocation.getSubmit().equals("add")) {
            carts.add(invocation.getItem());
        }
        else if(invocation.getSubmit().equals("remove")){
            carts.remove(invocation.getItem());
        }
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("carts", carts);
    }

    public static class CartInvocation extends Invocation {
        private String item;
        private String submit;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getSubmit() {
            return submit;
        }

        public void setSubmit(String submit) {
            this.submit = submit;
        }
    }

    public static void main(String[] args) {

    }
}
