/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.ejb3;

import org.jfox.ejb3.interceptor.InterceptorMethod;
import org.jfox.ejb3.interceptor.MethodInvokerInterceptorMethod;
import org.jfox.ejb3.security.SecurityContext;
import org.jfox.mvc.SessionContext;

import javax.annotation.security.RunAs;
import javax.security.auth.Subject;
import javax.transaction.TransactionManager;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJBInvocation {

    static ThreadLocal<EJBInvocation> currentThreadEJBInvocation = new ThreadLocal<EJBInvocation>();

    private EJBObjectId ejbObjectId;

    private EJBBucket bucket;

    private Object target = null;
    private Method interfaceMethod;
    private Method concreteMethod;
    private Object[] params;

    private TransactionManager tm;

    private SessionContext sessionContext;

    /**
     * 方法是否有 @RunAS
     */
    private boolean runAS = false;

    public static void setCurrent(EJBInvocation ejbInvocation){
        currentThreadEJBInvocation.set(ejbInvocation);
    }

    public static EJBInvocation current(){
        return currentThreadEJBInvocation.get();
    }

    public static void remove(){
        currentThreadEJBInvocation.remove();
    }

    public EJBInvocation(EJBObjectId ejbObjectId, EJBBucket bucket, Object target, Method interfaceMethod, Method concreteMethod, Object[] params, SessionContext securityContext) {
        this.ejbObjectId = ejbObjectId;
        this.bucket = bucket;
        this.target = target;
        this.interfaceMethod = interfaceMethod;
        this.concreteMethod = concreteMethod;
        this.params = params;
        this.sessionContext = securityContext;
    }

    public TransactionManager getTransactionManager() {
        return tm;
    }

    void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    public Collection<InterceptorMethod> getInterceptorMethods() {
        final List<InterceptorMethod> interceptorMethods = new ArrayList<InterceptorMethod>();
        // class level interceptor
        interceptorMethods.addAll(getBucket().getClassInterceptorMethods());
        // method level interceptor, 用 getConcreteMethod 做key
        interceptorMethods.addAll(getBucket().getMethodInterceptorMethods(getConcreteMethod()));
        // @AroundInvoke in Bean class
        interceptorMethods.addAll(getBucket().getBeanInterceptorMethods());
        // create BusinessInterceptorMethod for invoke method
        interceptorMethods.add(new MethodInvokerInterceptorMethod(getConcreteMethod()));
        return Collections.unmodifiableList(interceptorMethods);
    }

    public EJBObjectId getEJBObjectId(){
        return ejbObjectId;
    }

    private EJBBucket getBucket(){
        return bucket;
    }

    public ClassLoader getClassLoader() {
        return bucket.getModule().getModuleClassLoader();
    }

    public Object getTarget() {
        return target;
    }

    public boolean isStateful(){
        return bucket.isStateful();
    }

    public Method getInterfaceMethod(){
        return interfaceMethod;
    }

    /**
     * 反射调用的方法，这是接口的方法，无法得到 annotation
     * 要得到 annotation，必须使用 getInvocationMethod
     */
    public Method getConcreteMethod() {
        return concreteMethod;
    }

    /**
     * 给 InvocationContext.setParameters 用
     *
     * @param args args
     */
    public void setArgs(Object[] args) {
        this.params = args;
    }

    public Object[] getArgs() {
        return params;
    }

    protected SessionContext getSessionContext() {
        return sessionContext;
    }

    /**
     * 获取 Http Session 数据
     * @param attribute session key
     */
    public Object getSessionAttribute(String attribute){
        return getSessionContext().getAttribute(attribute);
    }

    public SecurityContext getSecurityContext() {
        return getSessionContext().getSecurityContext();
    }

    public Group getCallerGroup(){
        RunAs runAs = getBucket().getBeanClass().getAnnotation(RunAs.class);
        Subject subject = getSecurityContext().getSubject();
        if(runAs != null) {
            String runAsRole = runAs.value();
            String username = getSecurityContext().getPrincipalName();
            subject = SecurityContext.buildSubject(username, runAsRole);
        }
        if(subject == null) {
            return null;
        }
        return getSecurityContext().getCallerGroup(subject);
    }

    // 如果是 @RunAs Method，则需要根据 RunAs 指定的值构造 Subject
    public List<? extends Principal> getCallerRolesList(){
        Group group = getCallerGroup();
        if(group != null) {
            return Collections.list(group.members());
        }
        else {
            return new ArrayList<Principal>(0);
        }
    }


    public String toString() {
        return "EJBInvocation{EJB=" + getBucket().getBeanClass().getName() + ", method=" + getInterfaceMethod().getName() + "}";
    }

    // ------------
    private Iterator<EJBInvocationHandler> chain = null;

    void invokeByChain(Iterator<EJBInvocationHandler> chain) throws Exception{
        this.chain = chain;
        chainNext();
    }

    void chainNext() throws Exception{
        if(chain.hasNext()) {
            chain.next().process(this);
        }
    }

    private Object resultObject;

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        //将 Method result 放到 Invocation 中
        this.resultObject = resultObject;
    }

    /**
     * 存储与 EJBInvocation 相关的属性，可以用于在EJBInvocationHandler invoke onCaughtException onChainReturn 之间传递数据
     */
    private Map<String, Object> attribute = new HashMap<String, Object>();
    public void setAttribute(String key, Object value){
        attribute.put(key,value);
    }

    public Object getAttribute(String key) {
        return attribute.get(key);
    }
}
