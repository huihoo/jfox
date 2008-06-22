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

import org.apache.log4j.Logger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public abstract class JMSDestination implements Destination, Serializable, Runnable {

    Logger logger = Logger.getLogger(getClass());

    private static Comparator<Message> MESSAGE_COMPARATOR = new Comparator<Message>() {

        public int compare(Message msg1, Message msg2) {
            try {
                return Integer.valueOf(msg1.getJMSPriority()).compareTo(msg2.getJMSPriority());
            }
            catch (JMSException e) {
                return 0;
            }
        }
    };

    private final transient PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<Message>(1, MESSAGE_COMPARATOR);

    protected final List<MessageListener> listeners = new ArrayList<MessageListener>(2);

    private ExecutorService threadExecutor = Executors.newCachedThreadPool();

    protected final ReentrantLock lock = new ReentrantLock();

    // 等待非空消息的条件
    protected final Condition notEmptyMessageCondition = lock.newCondition();
    // 等待非空MessageListener的条件
    protected final Condition notEmptyListenerCondition = lock.newCondition();

    private volatile boolean started = false;

    private String name;

    private volatile long messageSend = 0;

    public JMSDestination(String name) {
        this.name = name;
        // start thread
        start();
    }

    public String toString() {
        return "JMSDestination [" + (isTopic() ? "Topic" : "Queue") + "] " + getName();
    }

    protected String getName() {
        return name;
    }

    public boolean equals(Object pObject) {
        if (this == pObject) return true;
        if (!(pObject instanceof JMSDestination)) return false;

        final JMSDestination jmsDestination = (JMSDestination)pObject;

        return !(name != null ? !name.equals(jmsDestination.name) : jmsDestination.name != null);

    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    public abstract boolean isTopic();

    public void putMessage(Message msg) {
        lock.lock();
        try {
            queue.offer(msg);
            // 交给线程池执行消息分发工作
            notEmptyMessageCondition.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 发送消息，由Queue/Topic实现
     *
     * @param message message
     */
    public abstract void sendMessage(Message message);

    public void registerMessageListener(MessageListener listener) {
        lock.lock();
        try {
            listeners.add(listener);
            notEmptyListenerCondition.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public void unregisterMessageListener(MessageListener listener) {
        lock.lock();
        try {
            listeners.remove(listener);
        }
        finally {
            lock.unlock();
        }
    }

    public Collection<MessageListener> getMessageListeners() {
        return Collections.unmodifiableCollection(listeners);
    }

    public void start() {
        started = true;
        threadExecutor.submit(this);
        logger.info("Destination started, name="+getName());
    }

    public void stop() {
        started = false;
        lock.lock();
        try {
            // 唤醒等待消息的线程
            notEmptyMessageCondition.signalAll();
            Thread.sleep(250);
            notEmptyListenerCondition.signalAll();
            Thread.sleep(250);
            // 使用 shutdownNow 可以强行中止线程
            threadExecutor.shutdownNow();
            logger.info("Destination stopped, name="+getName());
        }
        catch (Exception e) {
            logger.warn("Stop destination failed.", e);
        }
        finally {
            lock.unlock();
        }
    }

    public void run() {
        while (started) {
            lock.lock(); // 获得锁
            try {
                // 分发消息
                if (queue.isEmpty()) {
                    notEmptyMessageCondition.await();
                }
                if (listeners.isEmpty()) {
                    notEmptyListenerCondition.await();
                }
                if(started) {
                    final Message message = queue.take();
                    // 有可能是stop调用，所以需要判断 message == null
                    if (message != null) {
                        // 使用新线程发送消息
                        threadExecutor.execute(new Runnable(){
                            public void run() {
                                sendMessage(message);
                            }
                        });
                        messageSend++;
                    }
                }
            }
            catch (InterruptedException e) {
                logger.warn("Dispatcher Thread Interrupted.", e);
            }
            finally {
                lock.unlock();
            }
        }
    }

    /**
     * 有多少个 Client
     */
    public int countMessageConsumers() {
        return listeners.size();
    }

    /**
     * Queue/Topic中的消息数目
     */
    public int countMessageHold() {
        return queue.size();
    }

    /**
     * Queue/Topic 已经发送的消息的数目
     */
    public long countMessageSend() {
        return messageSend;
    }

}
