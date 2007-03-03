package net.sourceforge.jfox.ejb3;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJBInvocation {

    static ThreadLocal<EJBInvocation> currentThreadEJBInvocation = new ThreadLocal<EJBInvocation>();

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

    public EJBInvocation(EJBBucket bucket, Object target, Method interfaceMethod, Method concreteMethod, Object[] params) {
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

    public Collection<Method> getInterceptorMethods() {
        final List<Method> interceptorMethods = new ArrayList<Method>();
        // class level interceptor
        interceptorMethods.addAll(getBucket().getClassInterceptorMethods());
        // method level interceptor
        //TODO: 是用 getConcreteMethod 还是 getInterfaceMethod
        interceptorMethods.addAll(getBucket().getMethodInterceptorMethods(getConcreteMethod()));

        // method itself
        interceptorMethods.add(getConcreteMethod());
        return Collections.unmodifiableList(interceptorMethods);
    }

    public String getEJBname(){
        return bucket.getName();
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
