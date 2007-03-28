package org.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.transaction.TransactionManager;

import org.jfox.ejb3.interceptor.InterceptorMethod;
import org.jfox.ejb3.interceptor.BusinessInterceptorMethod;

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

    public static void setCurrent(EJBInvocation ejbInvocation){
        currentThreadEJBInvocation.set(ejbInvocation);
    }

    public static EJBInvocation current(){
        return currentThreadEJBInvocation.get();
    }

    public static void remove(){
        currentThreadEJBInvocation.remove();
    }

    public EJBInvocation(EJBObjectId ejbObjectId, EJBBucket bucket, Object target, Method interfaceMethod, Method concreteMethod, Object[] params) {
        this.ejbObjectId = ejbObjectId;
        this.bucket = bucket;
        this.target = target;
        this.interfaceMethod = interfaceMethod;
        this.concreteMethod = concreteMethod;
        this.params = params;
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
        interceptorMethods.add(new BusinessInterceptorMethod(getConcreteMethod()));
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
}
