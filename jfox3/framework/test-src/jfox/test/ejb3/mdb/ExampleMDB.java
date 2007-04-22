package jfox.test.ejb3.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import javax.jms.Message;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@MessageDriven(activationConfig =
        {
        @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
        @ActivationConfigProperty(propertyName="destination", propertyValue="testQ")
        })
public class ExampleMDB implements MessageListener {

    public void onMessage(Message recvMsg) {
      System.out.println("Received message: " + recvMsg);
   }

}
