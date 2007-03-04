package net.sourceforge.jfox.webservice.xfire;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.Invoker;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FirstTest {

    public static void main(String[] args) throws Exception {
        XFire xfire = XFireFactory.newInstance().getXFire();
        ServiceFactory factory = new ObjectServiceFactory(xfire.getTransportManager(), null);

        Service service = factory.create(IExample.class);
//        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, ExampleImpl.class);

        service.setInvoker(new Invoker() {
            public Object invoke(Method method, Object[] objects, MessageContext messageContext) throws XFireFault {
                System.out.println("Method: " + method + ", params: " + Arrays.toString(objects) + ", MessageContext: " + messageContext);
                return "Hello,Young!";
            }
        });

        xfire.getServiceRegistry().register(service);

        // Start the HTTP server
        XFireHttpServer server = new XFireHttpServer();
        server.setPort(8191);
        server.start();
    }
}
