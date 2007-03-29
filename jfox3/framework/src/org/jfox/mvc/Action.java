package org.jfox.mvc;

import org.jfox.framework.annotation.Exported;
import org.jfox.framework.component.Component;

/**
 * 所有Action的接口，Action实现Service接口，这样才能被外界所访问
 *
 * Action 会注册到 Framework，Action 注册的时候，
 * 会将Action添加到 ControllerServlet 的Action列表中
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface Action extends Component {

    void execute(InvocationContext invocationContext) throws Exception;

}