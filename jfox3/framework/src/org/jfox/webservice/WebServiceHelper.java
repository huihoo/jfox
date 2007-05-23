/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.webservice;

import java.net.MalformedURLException;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.client.XFireProxyFactory;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class WebServiceHelper {

    /**
     * 生成 WS 存根
     * @param endpoint ws endpoint url
     * @param wsInterface webservice interface
     */
    public static <T> T lookupWS(String endpoint, Class<T> wsInterface) {
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
