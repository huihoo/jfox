package net.sourceforge.jfox.manager;

import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;
import net.sourceforge.jfox.framework.annotation.Service;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Service(id="carts")
public class CartsAction extends ActionSupport {

    @ActionMethod(successView = "template/carts.vhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception{

    }
    
    public static void main(String[] args) {

    }
}
