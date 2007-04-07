package jfox.test;

import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Exported
public interface IUser extends Component, ComponentInitialization {

    String getName();

}
