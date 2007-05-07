/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
