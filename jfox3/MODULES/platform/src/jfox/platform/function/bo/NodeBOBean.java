package jfox.platform.function.bo;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import jfox.platform.function.dao.NodeDAO;
import jfox.platform.function.entity.Node;
import jfox.platform.infrastructure.DataAccessObject;
import jfox.platform.infrastructure.SuperBO;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless
@Local
public class NodeBOBean extends SuperBO implements NodeBO {

    @EJB
    NodeDAO nodeDAO;

    public DataAccessObject getDataAccessObject() {
        return nodeDAO;
    }

    public Node getNodeById(long id) {
        return nodeDAO.getNodeById(id);
    }

    public Node getNodeByBindAction(String bindAction) {
        return null;
    }

    public List<Node> getChildrenNodes(long parentNodeId) {
        return null;
    }

    public List<Node> getMenuNodesByModuleId(long moduleId){
        return null;
    }

    public void addFunction(){
        
    }
}
