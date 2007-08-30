package cn.iservicedesk.function.bo;

import java.util.List;

import cn.iservicedesk.function.entity.Node;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface NodeBO {

    Node getNodeById(long id);

    Node getNodeByBindAction(String bindAction);

    List<Node> getChildrenNodes(long parentNodeId);

    List<Node> getMenuNodesByModuleId(long moduleId);

}
