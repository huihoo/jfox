package org.jfox.ejb3.remote;

import java.lang.reflect.Method;

/**
 * 远程调用 EJB 时，在 Client 和 Server 之间传输的对象
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 7, 2008 11:03:00 PM
 */
public class EJBRemoteInvocation {

    /**
     * 传递时使用的Http URL SessionId，可以保持HttpSession状态
     */
    private String sessionId;

    private String ejbId;

    private Method method;

    private Object[] params;

    public static void main(String[] args) {

    }
}
