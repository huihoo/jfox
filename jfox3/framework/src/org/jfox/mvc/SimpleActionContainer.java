package org.jfox.mvc;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO: 管理 Action，就像 EJB Container
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 22, 2008 11:15:34 AM
 */
public class SimpleActionContainer implements ActionContainer {

    /**
     * chain to execute action invocation
     */
    private final List<ActionInvocationHandler> chain = new ArrayList<ActionInvocationHandler>();

    public void invokeAction(ActionContext actionContext) throws Exception {
        //TODO: chain.execute(), permission check
    }

    public static void main(String[] args) {
 
    }
}
