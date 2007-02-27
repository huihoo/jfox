package net.sourceforge.jfox.webservice.xfire;

import java.lang.reflect.Method;

import net.sourceforge.jfox.ejb3.EJBContainer;
import net.sourceforge.jfox.ejb3.StatelessEJBBucket;
import net.sourceforge.jfox.framework.annotation.Inject;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.component.ActiveComponent;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.InstantiatedComponent;
import net.sourceforge.jfox.framework.component.ComponentListener;
import net.sourceforge.jfox.framework.event.ComponentEvent;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.Invoker;

/**
 * 
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Service
public class JFoxXFireDelegate implements Invoker, InstantiatedComponent, ActiveComponent, ComponentListener {

    @Inject
    EJBContainer ejbContainer;
    
    /**
     * XFire instance
     */
    private XFire xfire;

    /**
     * XFire Service Factory
     */
    private ServiceFactory factory;

    public static JFoxXFireDelegate xFireDelegate = null;

    public static XFire getXFireInstance(){
        if(xFireDelegate == null) {
            throw new NullPointerException("XFire is not initialized!");
        }
        return xFireDelegate.xfire;
    }

    public void instantiated(ComponentContext componentContext) {
        xfire = XFireFactory.newInstance().getXFire();
        factory = new ObjectServiceFactory(xfire.getTransportManager(), null);
        xFireDelegate = this;
    }

    public void postPropertiesSet() {
        
    }

    public void componentChanged(ComponentEvent componentEvent) {
        //TODO: componentChanged
    }

    /**
     * 把 EJB 发布成 WebService
     * @param ejbBucket Statless EJB Bucket
     */
    public void exportEJBEndpoint(StatelessEJBBucket ejbBucket){
        org.codehaus.xfire.service.Service service = factory.create(ejbBucket.getBeanInterfaces()[0]);
        service.setInvoker(this);
        xfire.getServiceRegistry().register(service);
    }

    /**
     * 通过 EJBContainer 完成对 EJB 的调用
     * 
     * @param method 要调用的方法
     * @param params 参数
     * @param messageContext soap message context
     * @return method invocation result
     * @throws XFireFault
     */
    public Object invoke(Method method, Object[] params, MessageContext messageContext) throws XFireFault {
        String ejbName = getEJBNameByWebServiceEndpointInterface(messageContext.getService().getServiceInfo().getServiceClass());
        try {
            return ejbContainer.invokeEJB(ejbName, method, params);
        }
        catch(Exception e) {
            return new XFireFault(e);
        }
    }

    public String getEJBNameByWebServiceEndpointInterface(Class endpointInterface){
        //TODO: getEJBNameByWebServiceEndpointInterface
        //TODO: 有必要在 EJBContainer 中建立 EndpointInterface=>ejb-name的对应关系
        return null;
    }

}
