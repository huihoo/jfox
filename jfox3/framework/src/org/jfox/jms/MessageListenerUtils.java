package org.jfox.jms;

import java.lang.reflect.Method;
import javax.ejb.EJBException;
import javax.jms.MessageListener;
import javax.jms.Message;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MessageListenerUtils {
    private static final Method OnMessage;

    static {
        try {
            OnMessage = MessageListener.class.getMethod("onMessage", Message.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }
    }

    public static Method getOnMessageMethod(){
        return OnMessage;
    }

    public static void main(String[] args) {

    }
}
