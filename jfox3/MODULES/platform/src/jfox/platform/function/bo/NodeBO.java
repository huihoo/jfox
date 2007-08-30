package jfox.platform.function.bo;

import java.util.List;

import jfox.platform.function.entity.Node;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface NodeBO {

    Node getNodeById(long id);

    Node getNodeByBindAction(String bindAction);

    List<Node> getChildrenNodes(long parentNodeId);

    List<Node> getMenuNodesByModuleId(long moduleId);

}
