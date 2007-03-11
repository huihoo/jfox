package net.sourceforge.jfox.framework.component;

import net.sourceforge.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface TestComponent extends Component, ComponentInstantiation {

    void sayHello();

}
