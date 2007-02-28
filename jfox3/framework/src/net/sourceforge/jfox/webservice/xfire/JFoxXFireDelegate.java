package net.sourceforge.jfox.webservice.xfire;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jfox.ejb3.EJBBucket;
import net.sourceforge.jfox.ejb3.EJBContainer;
import net.sourceforge.jfox.ejb3.StatelessEJBBucket;
import net.sourceforge.jfox.ejb3.event.EJBLoadedComponentEvent;
import net.sourceforge.jfox.ejb3.event.EJBUnloadedComponentEvent;
import net.sourceforge.jfox.framework.annotation.Inject;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.component.ActiveComponent;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.ComponentListener;
import net.sourceforge.jfox.framework.component.InstantiatedComponent;
import net.sourceforge.jfox.framework.event.ComponentEvent;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.jaxws.JAXWSServiceFactory;
import org.codehaus.xfire.service.ServiceFactory;
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

    /**
     * EJB Endpoint interface => ejb name
     */
    private Map<String, String> endpointInterface2EJBNameMap = new HashMap<String, String>();

    public static XFire getXFireInstance(){
        if(xFireDelegate == null) {
            throw new NullPointerException("XFire is not initialized!");
        }
        return xFireDelegate.xfire;
    }

    public void instantiated(ComponentContext componentContext) {
        xfire = XFireFactory.newInstance().getXFire();
        //TODO: 需要建立新的 ServiceFactory，使用已经 load 的 endpointInterface
        factory = new JAXWSServiceFactory(xfire.getTransportManager());
        xFireDelegate = this;
    }

    public void postPropertiesSet() {
        // need do nothing
    }

    public void componentChanged(ComponentEvent componentEvent) {
        if(componentEvent instanceof EJBLoadedComponentEvent) {
            EJBBucket ejbBucket = ((EJBLoadedComponentEvent)componentEvent).getEJBBucket();
            if(ejbBucket instanceof StatelessEJBBucket){
                Class wsEndpointInterface = ((StatelessEJBBucket)ejbBucket).getWebServiceEndpointInterface();
                if(wsEndpointInterface != null){
                    // 把 EJB 发布成 WebService
                    endpointInterface2EJBNameMap.put(wsEndpointInterface.getName(), ejbBucket.getName());
                    org.codehaus.xfire.service.Service service = factory.create(wsEndpointInterface);
                    service.setInvoker(this);
                    xfire.getServiceRegistry().register(service);
                }
            }
        }
        else if(componentEvent instanceof EJBUnloadedComponentEvent) {
            EJBBucket ejbBucket = ((EJBUnloadedComponentEvent)componentEvent).getEJBBucket();
            if(ejbBucket instanceof StatelessEJBBucket){
                Class wsEndpointInterface = ((StatelessEJBBucket)ejbBucket).getWebServiceEndpointInterface();
                if(wsEndpointInterface != null){
                    endpointInterface2EJBNameMap.remove(wsEndpointInterface.getName());
                }
            }
        }

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

    private String getEJBNameByWebServiceEndpointInterface(Class endpointInterface){
        return endpointInterface2EJBNameMap.get(endpointInterface.getName());
    }

}
