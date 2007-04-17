package org.jfox.webservice;

import java.net.MalformedURLException;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.client.XFireProxyFactory;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class WSClient {

    /**
     * 生成 WS 存根
     * @param endpoint ws endpoint url
     * @param wsInterface webservice interface
     */
    public static <T> T createWSClient(String endpoint, Class<T> wsInterface) {
        try {
            Service serviceModel = new ObjectServiceFactory().create(wsInterface);
            XFireProxyFactory factory = new XFireProxyFactory();
            return (T)factory.create(serviceModel, endpoint);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

    }
}
