package jfox.test.ejb3.mdb;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

import org.jfox.ejb3.naming.JNDIContextHelper;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Stateless
@Remote
public class MessageSenderBean implements MessageSender {

    @Resource
    QueueConnectionFactory queueConnectionFactory;

    public void sendQuqueMessage() {
        try {
            // use injected jms connection factory
            //TODO: finish sendQuqueMessage
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public void sendTopicMessage() {
        try {
            // lookup jms connection factory by jndi
            TopicConnectionFactory tcf = (TopicConnectionFactory)JNDIContextHelper.lookup("defaultcf");
            //TODO: finish sendTopicMessage
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }
}
