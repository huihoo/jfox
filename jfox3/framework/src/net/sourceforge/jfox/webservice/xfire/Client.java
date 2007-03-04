package net.sourceforge.jfox.webservice.xfire;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Client {

    public static void main(String[] args) throws Exception {
        Service serviceModel = new ObjectServiceFactory().create(IExample.class);
        String url = "http://localhost:8191/IExample";

        XFireProxyFactory factory = new XFireProxyFactory();
        IExample example = (IExample)factory.create(serviceModel, url);

        System.out.println(example.sayHello("Hello,world!"));

    }
}
