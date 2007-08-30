package jfox.platform.function.dao;

import jfox.platform.function.entity.Node;
import jfox.platform.infrastructure.DataAccessObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface NodeDAO extends DataAccessObject {

    Node getNodeById(long id);
}
