/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.jms.destination;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;
import javax.jms.Message;
import javax.jms.JMSException;

/**
 * 由于只考虑支持本地 JMS，所以让 Destination 成为独立的 Container
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class QueueMessagePool {

    PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<Message>(0, new Comparator<Message>(){

        public int compare(Message o1, Message o2) {
            try {
                return Integer.valueOf(o1.getJMSPriority()).compareTo(Integer.valueOf(o1.getJMSPriority()));
            }
            catch(JMSException e) {
                return 0;
            }
        }
    });
    
}
