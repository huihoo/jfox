/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.Collection;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.transaction.TransactionManager;

import org.jfox.framework.annotation.Exported;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.component.ComponentUnregistration;
import org.jfox.framework.component.InterceptableComponent;
import org.jfox.framework.component.SingletonComponent;
import org.jfox.jms.MessageService;
import org.jfox.mvc.SessionContext;

/**
 * EJB3 容器
 * 负责 load, unload ejb, and invoke ejb method
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface EJBContainer extends Component, InterceptableComponent, ComponentInitialization, ActiveComponent, SingletonComponent, ComponentUnregistration {

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
     * @param sessionContext session context
     * @throws Exception exception
     * @return method result
     */
    Object invokeEJB(EJBObjectId ejbObjectId, Method method, Object[] params, SessionContext sessionContext) throws Exception;

}
