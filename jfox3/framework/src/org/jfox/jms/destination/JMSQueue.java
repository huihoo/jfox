/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */

/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.jfox.jms.message.TextMessageImpl;

/**
 * JMS Queue
 * 维护 JMS Message Queue，并且负责分发消息
 *
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */
public class JMSQueue extends JMSDestination implements Queue, Runnable {

    public JMSQueue(String name) {
        super(name);
    }

    public String getQueueName() throws JMSException {
        return getName();
    }

    public boolean isTopic() {
        return false;
    }

    public void sendMessage(Message message) {
        lock.lock();
        try {
            if (!listeners.isEmpty()) {
                MessageListener messageListener = listeners.iterator().next();
                messageListener.onMessage(message);
            }
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        JMSQueue queue = new JMSQueue("Test");
        queue.start();
        queue.registerMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("Message: " + message);
            }
        });
        TextMessage msg = new TextMessageImpl();
        msg.setText("Hello,World!");
        queue.putMessage(msg);

        Thread.sleep(2000);
        msg.setText("Hello, World2!");
        queue.putMessage(msg);

        Thread.sleep(1000);
        queue.stop();
    }
}
