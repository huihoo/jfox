/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.jfox.jms.message.TextMessageImpl;

/**
 * JMS Queue
 * 维护 JMS Message Queue，并且负责分发消息
 *
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */
public class JMSQueue extends JMSDestination implements Queue, Runnable {
    Logger logger = Logger.getLogger(JMSQueue.class);

    private static Comparator<Message> MESSAGE_COMPARATOR = new Comparator<Message>() {

        public int compare(Message msg1, Message msg2) {
            try {
                return Integer.valueOf(msg1.getJMSPriority()).compareTo(Integer.valueOf(msg2.getJMSPriority()));
            }
            catch (JMSException e) {
                return 0;
            }
        }
    };

    private final transient PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<Message>(1, MESSAGE_COMPARATOR);

    private final List<MessageListener> listeners = new ArrayList<MessageListener>(2);

    private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmptyMessage = lock.newCondition();
    private final Condition notEmptyListener = lock.newCondition();

    private volatile boolean started = false;

    public JMSQueue(String name) {
        super(name);
    }

    public String getQueueName() throws JMSException {
        return getName();
    }

    public boolean isTopic() {
        return false;
    }

    public void putMessage(Message msg) {
        lock.lock();
        try {
            queue.offer(msg);
            // 交给线程池执行消息分发工作
            notEmptyMessage.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public synchronized void registerMessageListener(MessageListener listener) {
        lock.lock();
        try {
            listeners.add(listener);
            notEmptyListener.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public synchronized void unregisterMessageListener(MessageListener listener) {
        lock.lock();
        try {
            listeners.remove(listener);
        }
        finally {
            lock.unlock();
        }
    }

    public void start() {
        started = true;
        //TODO: DAEMON thread
        threadExecutor.submit(this);
    }

    public void stop() {
        started = false;
        threadExecutor.shutdown();
    }

    public void run() {
        while (started) {
            lock.lock(); // 获得锁
            try {
                // 分发消息
                if (queue.isEmpty()) {
                    notEmptyMessage.await();
                }
                if (listeners.isEmpty()) {
                    notEmptyListener.await();
                }
                Message message = queue.take();
                MessageListener messageListener = listeners.get(0);
                messageListener.onMessage(message);
            }
            catch (InterruptedException e) {
                logger.warn("Dispatcher Thread Interrupted.", e);
            }
            finally {
                lock.unlock();
            }
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
    }
}
