package cn.iservicedesk.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

import cn.iservicedesk.common.JSONUtils;
import cn.iservicedesk.function.bo.ModuleBO;
import cn.iservicedesk.function.bo.NodeBO;
import cn.iservicedesk.function.entity.Module;
import cn.iservicedesk.function.entity.Node;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;

/**
 * 做日志记录、权限判断
 * <p/>
 * 所有 Action 从这里继承
 * <p/>
 * //TODO: 支持多语言 successView
 * //TODO: 支持 themes 选择
 * //TODO: 设置通用变量
 * //TODO: 检查新到消息
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class SuperAction extends ActionSupport {
    public static final String THEME_KEY = "theme";
    public static final String LANG_KEY = "lang";

    @EJB
    NodeBO nodeBO;
    @EJB
    ModuleBO moduleBO;

    private Module currentModule;
    private Node currentNode;


    protected void preAction(InvocationContext invocationContext) {
        super.preAction(invocationContext);
        
        PageContext pageContext = invocationContext.getPageContext();
        // set common attribute
        pageContext.setAttribute("__JSONUTILS__", JSONUtils.getInstance());

        // init currentModule currentNode, 根据 node.BindAction 得到 node
        String actionMethodName = invocationContext.getFullActionMethodName();
        //TODO: uncomment
//        currentNode = nodeBO.getNodeByBindAction(actionMethodName);
//        currentModule = moduleBO.getModuleById(currentNode.getModuleId());

        List<Module> allModules = moduleBO.getAllModules();
        pageContext.setAttribute("__ALL_MODULES__", allModules);
    }

    protected void postAction(InvocationContext invocationContext) {
        SessionContext sessionContext = invocationContext.getSessionContext();
        PageContext pageContext = invocationContext.getPageContext();
        // TODO: set default session

        // 设置主题
        String theme = (String)sessionContext.getAttribute(THEME_KEY);
        if(theme == null) {
            sessionContext.setAttribute(THEME_KEY, "VintageSugar");
        }
        sessionContext.setAttribute(LANG_KEY,"en_US");
        // 设置多语言
        String lang = (String)sessionContext.getAttribute(LANG_KEY);

        if (lang != null) {
            pageContext.setTargetView(lang + "/" + pageContext.getTargeView());
        }



        if(pageContext.hasBusinessException() || invocationContext.getPageContext().hasValidateException()) {
            // log action failed 
        }
        else {
            // log action successful
        }

        //TODO: uncomment
//        buildContextNodes(invocationContext);

    }

    /**
     * 根据上下文获得页面上的按钮，以及左边菜单
     * @param invocationContext
     */
    private void buildContextNodes(InvocationContext invocationContext){
        PageContext pageContext = invocationContext.getPageContext();

        Map<String, List<Node>> nodeGroups = new HashMap<String, List<Node>>();
        List<Node> menuNodes = new ArrayList<Node>();

        // menuNodes in current Module
        List<Node> moduleMenuNodes = nodeBO.getMenuNodesByModuleId(currentModule.getId());
        menuNodes.addAll(moduleMenuNodes);

        // child nodes in current node
        List<Node> childrenNodes = nodeBO.getChildrenNodes(currentNode.getId());
        for(Node node : childrenNodes){
            if(node.isMenu()) { // 在 button node 下也可以注册 menu node
                menuNodes.add(node);
            }
            else {
                String nodeGroup = node.getNodeGroup();
                List<Node> nodes = nodeGroups.get(nodeGroup);
                if(nodes == null) {
                    nodes = new ArrayList<Node>();
                    nodeGroups.put(nodeGroup, nodes);
                }
                nodes.add(node);
            }
        }

        pageContext.setAttribute("_MENU_NODES_", menuNodes);
        // get buttonNodes
        pageContext.setAttribute("_BUTTON_NODE_GROUPS_", nodeGroups);
    }

    /**
     * 调用的节点
     */
    protected Node getCurrentNode(){
        return currentNode;
    }

    /**
     * 调用所在的模块
     */
    protected Module getCurrentModule() {
        return currentModule;
    }

    public static void main(String[] args) {

    }
}
