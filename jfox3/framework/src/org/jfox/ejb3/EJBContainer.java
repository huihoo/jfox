package org.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.Collection;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.transaction.TransactionManager;

import org.jfox.framework.annotation.Exported;
import org.jfox.framework.component.Component;
import org.jfox.ejb3.security.SecurityContext;
import org.jfox.jms.MessageService;

/**
 * EJB3 容器
 * 负责 load, unload ejb, and invoke ejb method
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface EJBContainer extends Component {

    public static final String JAVA_COMP_ENV = "java:comp/env";
    
    /**
     * 列表所有 EJB Bucket
     */
    Collection<EJBBucket> listBuckets();

    /**
     * 返回 EJB Bucket
     * @param name ejb name
     */
    EJBBucket getEJBBucket(String name);

    /**
     * 通过接口类取 EJBBucket，再解析 @EJB 注入的时候要用到
     * @param interfaceClass bean interface
     */
    Collection<EJBBucket> getEJBBucketByBeanInterface(Class interfaceClass);

    /**
     * 容器使用的事物处理器
     */
    TransactionManager getTransactionManager();

    /**
     * 定时服务器
     */
    TimerService getTimerService();

    /**
     * JNDI Naming Service
     */
    Context getNamingContext();

    /**
     * JMS Message Service
     */
    MessageService getMessageService();

    /**
     * get JTA Transaction Timeout
     */
    int getTransactionTimeout();

    /**
     * set JTA Transaction Timeout
     * @param timeout timeout in seconds
     */
    void setTransactionTimeout(int timeout);

    /**
     * 调用EJB方法，通过方法拦截提供事务支持
     * 构造 ejb invocation，并且获得 chain，然后发起调用
     * 
     * @param ejbObjectId ejb object id
     * @param method 要执行的方法
     * @param params 参数 @throws NoSuchEJBException if no such ejb
     * @param securityContext security context
     * @throws Exception exception
     * @return method result
     */
    Object invokeEJB(EJBObjectId ejbObjectId, Method method, Object[] params, SecurityContext securityContext) throws Exception;

    /**
     * 通过该方法来完成事务的发起
     *
     * @param method method to invoke
     * @param params parameters
     */
    boolean preInvoke(Method method, Object[] params);

    /**
     * 该方法负责事务的结束
     * 
     * @param method method invoked
     * @param params parameters
     * @param result result
     * @param exception if throws exception
     */
    Object postInvoke(Method method, Object[] params, Object result, Throwable exception);

}
