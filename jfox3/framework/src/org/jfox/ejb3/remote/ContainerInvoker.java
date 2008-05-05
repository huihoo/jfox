/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.jfox.ejb3.remote;

import org.jfox.ejb3.EJBObjectId;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.SingletonComponent;
import org.jfox.mvc.SessionContext;

import java.lang.reflect.Method;

/**
 * 超类，用来定义远程调用EJB容器的API
 * 可以实现 Java Serialization, Hessian, XFire 甚至 Socket 的 ContainerInvoker
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ContainerInvoker extends ActiveComponent, SingletonComponent {

    Object invokeEJB(EJBObjectId ejbObjectId, Method method, Object[] params, SessionContext sessionContext) throws Exception;
    
}
