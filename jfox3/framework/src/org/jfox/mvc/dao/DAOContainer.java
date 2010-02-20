package code.google.webactioncontainer.dao;

import code.google.jcontainer.AbstractContainer;
import code.google.jcontainer.annotation.Container;

/**
 * Component to hold all DataAccessObject components
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Container(name = "DAOContainer",
        componentType = DataAccessObject.class
)
public class DAOContainer extends AbstractContainer{

}
