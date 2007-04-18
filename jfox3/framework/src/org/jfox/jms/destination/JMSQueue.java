/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package org.jfox.jms.destination;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.Comparator;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Message;

/**
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class JMSQueue extends JMSDestination implements Queue {

    private static Comparator<Message> MESSAGE_COMPARATOR = new Comparator<Message>(){

        public int compare(Message msg1, Message msg2) {
            try {
                return Integer.valueOf(msg1.getJMSPriority()).compareTo(Integer.valueOf(msg2.getJMSPriority()));
            }
            catch(JMSException e) {
                return 0;
            }
        }
    };

    //TODO: hold a destination container
    private transient MessagePool msgPool = new QueueMessagePool() ;

    private transient PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<Message>(0, MESSAGE_COMPARATOR);

    private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    public JMSQueue(String name) {
		super(name);
	}

	public String getQueueName() throws JMSException {
		return getName();
	}

	public boolean isTopic() {
		return false;
	}

    public synchronized void putMessage(Message msg){
        queue.offer(msg);
        // 交给线程执行分发工作
        threadExecutor.submit(new Callable<Object>() {
            public Object call() throws Exception {
                // 分发消息
                System.out.println("Hello,World!");
                return null;
            }
        });
    }

    public synchronized Message popMessage() {
        try {
            return queue.take();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stop(){
        threadExecutor.shutdown();
    }
    
    public static void main(String[] args) {

	}
}
