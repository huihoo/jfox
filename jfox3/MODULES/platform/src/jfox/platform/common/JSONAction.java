package jfox.platform.common;

import jfox.platform.infrastructure.SuperAction;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.InvocationContext;

/**
 * 所有异步请求，都通过这个 Action 执行
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Service(id="async")
public class JSONAction extends SuperAction {

    public void createModule(InvocationContext invocationContext) throws Exception {

    }

    public static void main(String[] args) {

    }
}
