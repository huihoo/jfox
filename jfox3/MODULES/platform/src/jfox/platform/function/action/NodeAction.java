package jfox.platform.function.action;

import javax.ejb.EJB;

import jfox.platform.function.bo.NodeBO;
import jfox.platform.function.entity.Node;
import jfox.platform.infrastructure.SuperAction;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id="node")
public class NodeAction extends SuperAction {

    @EJB
    NodeBO nodeBO;

    @ActionMethod(name="newview", successView = "function/new_node.vhtml")
    public  void newViewFunction(InvocationContext invocationContext) throws Exception {

//        System.out.println("!!! Hello, " + invocationContext.getFullActionMethodName());

    }

    @ActionMethod(name="new", successView = "function/new_node.vhtml")
    public synchronized void newFunction(InvocationContext invocationContext) throws Exception {

//        System.out.println("!!! Hello, " + invocationContext.getFullActionMethodName());

    }

    @ActionMethod(name="add", successView = "function/list_node.vhtml")
    public void addFunction(InvocationContext invocationContext) throws Exception {

    }

    @ActionMethod(name="get", successView = "function/view_node.vhtml", invocationClass = GetFunctionInvocation.class)
    public void getFunction(InvocationContext invocationContext) throws Exception {
        GetFunctionInvocation invocation = (GetFunctionInvocation)invocationContext.getInvocation();
        long funcId = invocation.getId();
        Node node = new Node(); // functionBO.getFunctionById(functionId);
        node.setName("Add User");

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("function", node);
    }

    public static class GetFunctionInvocation extends Invocation {

        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class NewModuleInvocation extends GetFunctionInvocation {

    }
}