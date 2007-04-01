package jfox.test.kernel.component;

import org.jfox.framework.annotation.Exported;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInstantiation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface TestComponent extends Component, ComponentInstantiation {

    void sayHello();

}
