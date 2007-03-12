package net.sourceforge.jfox.demo.action;

import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id="checkbox")
public class CheckboxAction extends ActionSupport {

    @ActionMethod(successView = "template/checkbox.vhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception{
        // do nothing, just show template
    }

    @ActionMethod(successView = "template/checkbox.vhtml", invocationClass = CheckboxInvocation.class)
    public void doPostSubmit(InvocationContext invocationContext) throws Exception{
        CheckboxInvocation invocation = (CheckboxInvocation)invocationContext.getInvocation();
        String[] fruits = invocation.getFruit();
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("fruits", fruits);
    }

    public static class CheckboxInvocation extends Invocation {
        String[] fruit = new String[0];

        public String[] getFruit() {
            return fruit;
        }

        public void setFruit(String[] fruit) {
            this.fruit = fruit;
        }
    }

    public static void main(String[] args) {

    }
}
