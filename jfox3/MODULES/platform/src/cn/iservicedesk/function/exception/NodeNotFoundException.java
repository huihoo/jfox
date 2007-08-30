package cn.iservicedesk.function.exception;

import cn.iservicedesk.infrastructure.BusinessException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class NodeNotFoundException extends BusinessException {

    public NodeNotFoundException(String msg) {
        super(msg);
    }

    public NodeNotFoundException(Exception e) {
        super(e);
    }

    public NodeNotFoundException(String msg, Exception e) {
        super(msg, e);
    }

    public static void main(String[] args) {

    }
}
