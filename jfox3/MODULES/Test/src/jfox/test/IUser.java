package jfox.test;

import net.sourceforge.jfox.framework.component.Component;
import net.sourceforge.jfox.framework.component.ComponentInstantiation;
import net.sourceforge.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Exported
public interface IUser extends Component, ComponentInstantiation {

    String getName();

}
