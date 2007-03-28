package org.jfox.framework.component;

import org.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface TestComponent extends Component, ComponentInstantiation {

    void sayHello();

}
