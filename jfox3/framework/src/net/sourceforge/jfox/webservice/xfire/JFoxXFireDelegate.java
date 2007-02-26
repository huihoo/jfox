package net.sourceforge.jfox.webservice.xfire;

import java.lang.reflect.Method;

import net.sourceforge.jfox.ejb3.EJBContainer;
import net.sourceforge.jfox.ejb3.StatelessEJBBucket;
import net.sourceforge.jfox.ejb3.EJBBucket;
import net.sourceforge.jfox.framework.annotation.Inject;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.component.ActiveComponent;
import net.sourceforge.jfox.framework.component.InstantiatedComponent;
import net.sourceforge.jfox.framework.component.ComponentContext;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.invoker.Invoker;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

/**
 * 
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Service
public class JFoxXFireDelegate implements Invoker, InstantiatedComponent, ActiveComponent {

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

    public void instantiated(ComponentContext componentContext) {
        xfire = XFireFactory.newInstance().getXFire();
        factory = new ObjectServiceFactory(xfire.getTransportManager(), null);
    }

    public void postPropertiesSet() {
        
    }

    public XFire getXFireInstance(){
        return xfire;
    }

    public ServiceFactory getServiceFactory(){
        return factory;
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
     * @param method 要调用的方法
     * @param params 参数
     * @param messageContext soap message context
     * @return method invocation result
     * @throws XFireFault
     */
    public Object invoke(Method method, Object[] params, MessageContext messageContext) throws XFireFault {
//        return ejbContainer.invokeEJB("ejb-name",method, params);
        return null;
    }

    public EJBBucket getStatelessEJBBucketByWebServiceEndpointInterface(Class endpointInterface){
        //TODO: getStatelessEJBBucketByWebServiceEndpointInterface
        //TODO: 有必要在 EJBContainer 中建立 EndpointInterface=>EJBBucket的对应关系
        return null;
    }

}
