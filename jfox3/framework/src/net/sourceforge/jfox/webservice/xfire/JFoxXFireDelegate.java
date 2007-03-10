package net.sourceforge.jfox.webservice.xfire;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.jws.WebService;
import javax.xml.namespace.QName;

import net.sourceforge.jfox.ejb3.EJBBucket;
import net.sourceforge.jfox.ejb3.EJBContainer;
import net.sourceforge.jfox.ejb3.StatelessBucket;
import net.sourceforge.jfox.ejb3.EJBObjectId;
import net.sourceforge.jfox.ejb3.event.EJBLoadedComponentEvent;
import net.sourceforge.jfox.ejb3.event.EJBUnloadedComponentEvent;
import net.sourceforge.jfox.framework.annotation.Inject;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.component.ActiveComponent;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.ComponentListener;
import net.sourceforge.jfox.framework.component.InstantiatedComponent;
import net.sourceforge.jfox.framework.component.ComponentUnregistration;
import net.sourceforge.jfox.framework.event.ComponentEvent;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.annotations.AnnotationException;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.Invoker;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.apache.log4j.Logger;

/**
 * 使用 XFire 实现 Web Service
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Service
public class JFoxXFireDelegate  implements Invoker, InstantiatedComponent, ActiveComponent, ComponentUnregistration, ComponentListener {

    private static final Logger logger = Logger.getLogger(JFoxXFireDelegate.class);

    @Inject
    EJBContainer ejbContainer;

    /**
     * XFire instance
     */
    private XFire xfire;

    /**
     * EJB XFire Service Factory
     */
    private EJBServiceFactory factory;

    public static JFoxXFireDelegate xFireDelegate = null;

    /**
     * EJB Endpoint interface => ejb name
     * 以便 webservice 调用 ejb 的时候，能够根据 interface 找到 ejb
     */
    private Map<String, String> endpointInterface2EJBNameMap = new HashMap<String, String>();

    public static XFire getXFireInstance() {
        if (xFireDelegate == null) {
            throw new NullPointerException("XFire is not initialized!");
        }
        return xFireDelegate.xfire;
    }

    public void instantiated(ComponentContext componentContext) {
        xfire = XFireFactory.newInstance().getXFire();
        //是否可以考虑直接使用 XFire 的 JAXAWSServiceFactory
        factory = new EJBServiceFactory(xfire.getTransportManager());
        xFireDelegate = this;
    }

    public void postPropertiesSet() {
        // need do nothing
    }


    public void preUnregister(ComponentContext context) {

    }

    public void postUnregister() {
        
    }

    public void componentChanged(ComponentEvent componentEvent) {
        if (componentEvent instanceof EJBLoadedComponentEvent) {
            EJBBucket ejbBucket = ((EJBLoadedComponentEvent)componentEvent).getEJBBucket();
            if (ejbBucket instanceof StatelessBucket) {
                Class wsEndpointInterface = ((StatelessBucket)ejbBucket).getWebServiceEndpointInterface();
                if (wsEndpointInterface != null) {
                    // 把 EJB 发布成 WebService
                    endpointInterface2EJBNameMap.put(wsEndpointInterface.getName(), ejbBucket.getEJBName());
                    // create xfire service by stateless bucket
                    org.codehaus.xfire.service.Service service = factory.create((StatelessBucket)ejbBucket);
                    service.setInvoker(this);
                    if(xfire.getServiceRegistry().hasService(service.getSimpleName()) || xfire.getServiceRegistry().hasService(service.getName())) {
                        logger.warn("Web Service with QName " + service.getName() + " has already beean registered!");
                        return;
                    }
                    xfire.getServiceRegistry().register(service);
                    logger.info("Web Service with QName " + service.getName() + "  registered successfully!");
                }
            }
        }
        else if (componentEvent instanceof EJBUnloadedComponentEvent) {
            EJBBucket ejbBucket = ((EJBUnloadedComponentEvent)componentEvent).getEJBBucket();
            if (ejbBucket instanceof StatelessBucket) {
                Class wsEndpointInterface = ((StatelessBucket)ejbBucket).getWebServiceEndpointInterface();
                if (wsEndpointInterface != null) {
//                    xfire.getServiceRegistry().unregister();
                    endpointInterface2EJBNameMap.remove(wsEndpointInterface.getName());
                    logger.info("Web Service with endpoint interface " + wsEndpointInterface.getName() + "  unregistered!");
                }
            }
        }

    }

    /**
     * 通过 EJBContainer 完成对 EJB 的调用
     *
     * @param method         要调用的方法
     * @param params         参数
     * @param messageContext soap message context
     * @return method invocation result
     * @throws XFireFault
     */
    public Object invoke(Method method, Object[] params, MessageContext messageContext) throws XFireFault {
        String ejbName = getEJBNameByWebServiceEndpointInterface(messageContext.getService().getServiceInfo().getServiceClass());
        try {
            // stateless, 直接用 ejb name 做 ejb id
            return ejbContainer.invokeEJB(new EJBObjectId(ejbName), method, params);
        }
        catch (Exception e) {
            return new XFireFault(e);
        }
    }

    private String getEJBNameByWebServiceEndpointInterface(Class endpointInterface) {
        return endpointInterface2EJBNameMap.get(endpointInterface.getName());
    }


    /**
     * EJB Service Factory
     */
    class EJBServiceFactory extends ObjectServiceFactory {

        public EJBServiceFactory(TransportManager transportManager) {
            super(transportManager);
        }

        org.codehaus.xfire.service.Service create(StatelessBucket ejbBucket) {
            Map<String,Object> properties = new HashMap<String,Object>();
            WebService wsAnnotation = ejbBucket.getWebServiceAnnotation();
            Class endpointInterface = ejbBucket.getWebServiceEndpointInterface();

            String serviceName = wsAnnotation.serviceName();
            if (serviceName == null || serviceName.trim().length() == 0) {
                serviceName = makeServiceNameFromClassName(endpointInterface);
            }

            String serviceNameSpace = wsAnnotation.targetNamespace();
            if (serviceNameSpace == null || serviceNameSpace.trim().length() == 0) {
                serviceNameSpace = NamespaceHelper.makeNamespaceFromClassName(ejbBucket.getBeanClass().getName(), "http");
            }

            String portType = wsAnnotation.name();
            if (portType == null || portType.trim().length() == 0) {
                portType = serviceName + "PortType";
            }

            properties.put(PORT_TYPE, new QName(serviceNameSpace, portType));
            String pname = wsAnnotation.portName();
            if (pname != null && pname.length() > 0) {
                properties.put(PORT_NAME, new QName(serviceNameSpace, pname));
            }

            org.codehaus.xfire.service.Service service = create(endpointInterface, serviceName, serviceNameSpace, properties);
            String wsdl = wsAnnotation.wsdlLocation();
            if (wsdl != null && wsdl.length() > 0) {
                try {
                    service.setWSDLWriter(new ResourceWSDL(wsdl));
                }
                catch (IOException e) {
                    throw new AnnotationException("Couldn't load wsdl from " + wsdl, e);
                }
            }
            return service;
        }

        @SuppressWarnings("unchecked")
        public org.codehaus.xfire.service.Service create(Class clazz, QName name, URL wsdlUrl, Map properties) {
            if (properties == null) {
                properties = new HashMap();
            }
            properties.put(AnnotationServiceFactory.ALLOW_INTERFACE, Boolean.TRUE);
            return super.create(clazz, name, wsdlUrl, properties);
        }

    }
}
