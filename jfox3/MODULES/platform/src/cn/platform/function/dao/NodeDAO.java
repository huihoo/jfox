package cn.iservicedesk.function.dao;

import cn.iservicedesk.function.entity.Node;
import cn.iservicedesk.infrastructure.DataAccessObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface NodeDAO extends DataAccessObject {

    Node getNodeById(long id);
}
