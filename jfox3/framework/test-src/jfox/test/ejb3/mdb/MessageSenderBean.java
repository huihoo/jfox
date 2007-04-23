package jfox.test.ejb3.mdb;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.annotation.Resource;
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
    SessionContext sessionContext;

    public void sendQuqueMessage() {
        QueueConnectionFactory qcf = (QueueConnectionFactory)sessionContext.lookup("defaultcf");
        //TODO: finish sendQuqueMessage

    }

    public void sendTopicMessage() {
        try {
            TopicConnectionFactory tcf = (TopicConnectionFactory)JNDIContextHelper.lookup("defaultcf");
            //TODO: finish sendTopicMessage
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }
}
