package org.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.ejb.MessageDriven;
import javax.ejb.RemoveException;
import javax.ejb.TimerService;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.jfox.ejb3.dependent.FieldEJBDependence;
import org.jfox.ejb3.dependent.FieldResourceDependence;
import org.jfox.ejb3.security.SecurityContext;
import org.jfox.entity.dependent.FieldPersistenceContextDependence;
import org.jfox.framework.component.Module;
import org.jfox.jms.MessageListenerUtils;
import org.jfox.jms.MessageService;
import org.jfox.jms.destination.JMSDestination;

/**
 * Container of MessageDriven EJB，store all Meta data, and as EJB Factory
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MDBBucket extends SessionBucket implements PoolableObjectFactory, MessageListener {

    private EJBObjectId ejbObjectId;

    private MessageDrivenEJBContextImpl messageDrivenEJBContext;

    /**
     * cache EJB proxy stub, stateless EJB have only one stub
     */
    private EJBObject proxyStub = null;

    /**
     * cache EJB instances
     */
    private final GenericObjectPool pool = new GenericObjectPool(this);

    private JMSDestination destination;
    private boolean isQueue = true;

    public MDBBucket(EJBContainer container, Class<?> beanClass, Module module) {
        super(container, beanClass, module);

        introspectMDB();
        injectClassDependents();
    }

    private void introspectMDB() {
        MessageDriven messageDriven = getBeanClass().getAnnotation(MessageDriven.class);
        String name = messageDriven.name();
        if (name.equals("")) {
            name = getBeanClass().getSimpleName();
        }
        setEJBName(name);

        String mappedName = messageDriven.mappedName();
        if (mappedName.equals("")) {
            if (isRemote()) {
                addMappedName(name + "/remote");
            }
            if (isLocal()) {
                addMappedName(name + "/local");
            }
        }
        else {
            addMappedName(mappedName);
        }

        setDescription(messageDriven.description());

        // initialize Message Service
        Map<String, String> activationConfigMap = new HashMap<String, String>();
        ActivationConfigProperty[] activationConfigProperties = messageDriven.activationConfig();
        for (ActivationConfigProperty property : activationConfigProperties) {
            activationConfigMap.put(property.propertyName(), property.propertyValue());
        }
        String destination = activationConfigMap.get("destination");
        String destinationType = activationConfigMap.get("destinationType");
        try {
            if (destinationType.equals(Topic.class.getName())) { //Topic
                isQueue = false;
                MessageService messageService = getEJBContainer().getMessageService();
                this.destination = messageService.createTopic(destination);
            }
            else { // Queue
                isQueue = true;
                MessageService messageService = getEJBContainer().getMessageService();
                this.destination = messageService.createQueue(destination);
            }
        }
        catch (Exception e) {
            throw new EJBException("Could not initialize MessageDriven Bean: " + getEJBName(), e);
        }
    }

    public JMSDestination getDestination() {
        return destination;
    }

    public boolean isLocal() {
        return true;
    }

    public boolean isRemote() {
        return false;
    }

    public boolean isSession() {
        return false;
    }

    /**
     * 从 Pool 中得到一个新的 Bean 实例
     *
     * @param ejbObjectId ejb object id
     * @throws javax.ejb.EJBException exception
     */
    public AbstractEJBContext getEJBContext(EJBObjectId ejbObjectId) throws EJBException {
        try {
            EJBContextImpl ejbContext = (EJBContextImpl)pool.borrowObject();
            return ejbContext;
        }
        catch (Exception e) {
            throw new EJBException("Create EJBContext failed.", e);
        }
    }

    /**
     * 将实例返回给 pool
     *
     * @param ejbContext ejb context
     */
    public void reuseEJBContext(AbstractEJBContext ejbContext) {
        try {
            pool.returnObject(ejbContext);
        }
        catch (Exception e) {
            throw new EJBException("Return EJBContext to pool failed!", e);
        }
    }

    public AbstractEJBContext createEJBContext(EJBObjectId ejbObjectId, Object instance) {
        if (messageDrivenEJBContext == null) {
            messageDrivenEJBContext = new MessageDrivenEJBContextImpl(ejbObjectId, instance);
        }
        return messageDrivenEJBContext;
    }

    /**
     * 每个Stateless Bucket只有一个 EJBObjectId
     */
    public synchronized EJBObjectId createEJBObjectId() {
        if (ejbObjectId == null) {
            ejbObjectId = new EJBObjectId(getEJBName());
        }
        return ejbObjectId;
    }

    public boolean isStateless() {
        return false;
    }

    public boolean isWebService() {
        return false;
    }

    public void start() {
        // register MessageListener to Destination
        getDestination().registerMessageListener(this);
/*
        try {

            MessageService connectionFactory = getEJBContainer().getMessageService();
            if (isQueue) {
                QueueConnection connection = connectionFactory.createQueueConnection();
                QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                QueueReceiver receiver = session.createReceiver((Queue)getDestination());
                receiver.setMessageListener(this);
                connection.start();

            }
            else {
                TopicConnection connection = connectionFactory.createTopicConnection();
                TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                TopicSubscriber receiver = session.createSubscriber((Topic)getDestination());
                receiver.setMessageListener(this);
                connection.start();
            }
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
*/
        
    }

    /**
     * destroy bucket, invoke when container unload ejb
     */
    public void stop() {
        logger.debug("Destroy EJB: " + getEJBName() + ", Module: " + getModule().getName());
        try {
            //maybe close by JMS Connection
            getDestination().unregisterMessageListener(this);
            pool.clear();
            pool.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成基于动态代理的 Stub
     */
    public synchronized EJBObject createProxyStub() {
        if (proxyStub == null) {
            proxyStub = super.createProxyStub();
        }
        return proxyStub;
    }

    public void onMessage(Message message) {
        try {
            getEJBContainer().invokeEJB(createEJBObjectId(), MessageListenerUtils.getOnMessageMethod(), new Object[]{message}, new SecurityContext());
        }
        catch (EJBException e) {
            throw e;
        }
        catch (Exception e) {
            throw new EJBException(e);
        }

    }

    //--- jakarta commons-pool PoolableObjectFactory ---
    public Object makeObject() throws Exception {
        Object obj = getBeanClass().newInstance();
        AbstractEJBContext ejbContext = createEJBContext(createEJBObjectId(), obj);
        // post construct
        for (Method postConstructMethod : getPostConstructMethods()) {
            logger.debug("PostConstruct method for ejb: " + getEJBName() + ", method: " + postConstructMethod);
            postConstructMethod.invoke(ejbContext.getEJBInstance());
        }

        // 注入 @EJB
        for (FieldEJBDependence fieldEJBDependence : fieldEJBdependents) {
            fieldEJBDependence.inject(ejbContext);
        }

        // 注入 @EJB
        for (FieldResourceDependence fieldResourceDependence : fieldResourcedependents) {
            fieldResourceDependence.inject(ejbContext);
        }

        // 注入 @PersistenceContext
        for (FieldPersistenceContextDependence fieldPersistenceContextDependence : fieldPersistenceContextDependences) {
            fieldPersistenceContextDependence.inject(ejbContext);
        }

        //返回 EJBContext
        return ejbContext;
    }

    public boolean validateObject(Object obj) {
        return true;
    }

    public void activateObject(Object obj) throws Exception {
    }

    public void passivateObject(Object obj) throws Exception {
    }

    public void destroyObject(Object obj) throws Exception {
        for (Method preDestroyMethod : getPreDestroyMethods()) {
            logger.debug("PreDestory method for ejb: " + getEJBName() + ", method: " + preDestroyMethod);
            preDestroyMethod.invoke(((AbstractEJBContext)obj).getEJBInstance());
        }
    }

    // EJBContext Implementation
    @SuppressWarnings({"deprecation"})
    public class MessageDrivenEJBContextImpl extends EJBContextImpl {

        public MessageDrivenEJBContextImpl(EJBObjectId ejbObjectId, Object ejbInstance) {
            super(ejbObjectId, ejbInstance);
        }

        public TimerService getTimerService() throws IllegalStateException {
            return null;
        }

        // SessionContext
        public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
            return (T)proxyStub;
        }

        // EJBObject & EJBLocalObject
        public void remove() throws RemoveException {
            try {
                destroyObject(getEJBInstance());
            }
            catch (Exception e) {
                String msg = "Remove EJB instance failed!";
                logger.warn(msg, e);
                throw new RemoveException(msg);
            }
        }
    }

}
