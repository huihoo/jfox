package cn.iservicedesk.function.bo;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import cn.iservicedesk.function.dao.NodeDAO;
import cn.iservicedesk.function.entity.Node;
import cn.iservicedesk.infrastructure.DataAccessObject;
import cn.iservicedesk.infrastructure.SuperBO;

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
