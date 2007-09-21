/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.component.ComponentUnregistration;
import org.jfox.framework.component.SingletonComponent;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.validate.ValidateException;
import org.jfox.mvc.validate.Validators;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.ClassUtils;

/**
 * Action super class
 * <p/>
 * 子类实现 doGetXXX doPostXXX 方法，如 public void doGetIndex(InvocationContext invocationContext) throws Exception
 * XXX 是不区分大小写，所以不能有同名的 XXX 方法
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class ActionSupport implements Action, ComponentInitialization, ComponentUnregistration, ActiveComponent, SingletonComponent {

    public static final String PAGE_VIEW_PATH = "J_PAGE_VIEW";

    /**
     * post action prefix, invoked method will be "DOPOST + %ACTION_NAME%"
     */
    public static final String POST_METHOD_PREFIX = "POST_";
    public static final String GET_METHOD_PREFIX = "GET_";

    private String moduleDirName;

    private ComponentContext context;

    /**
     * 声明的 ActionMethod
     * action method name => Method
     */
    private Map<String, Method> actionMap = new ConcurrentHashMap<String, Method>();

    protected Logger logger = Logger.getLogger(this.getClass());

    public void postContruct(ComponentContext componentContext) {
        this.context = componentContext;
    }

    public void postInject() {
        Method[] actionMethods = AnnotationUtils.getAnnotatedMethods(this.getClass(), ActionMethod.class);
        for (Method actionMethod : actionMethods) {
            if (actionMethod.getReturnType().equals(void.class)
                    && actionMethod.getParameterTypes().length == 1
                    && actionMethod.getParameterTypes()[0].equals(InvocationContext.class)) {
                // 统一转换成大写
                // TODO: 分析 actionMethod 对应的 @ActionMethod，并根据 @ActionMethod支持的 HttpMethod 作为 key
                ActionMethod actionMethodAnnotation = actionMethod.getAnnotation(ActionMethod.class);
                String actionMethodName = actionMethodAnnotation.name();
                String actionName = (actionMethodName == null || actionMethodName.trim().equals("")) ? actionMethod.getName() : actionMethodName.trim();
                //TODO: 判断 action key 是否已经存在，如果存在，进行 ERROR，抽象 registerPostAction() 方法
                if(actionMethodAnnotation.httpMethod().equals(ActionMethod.HttpMethod.GET)){
                    actionMap.put((GET_METHOD_PREFIX + actionName).toUpperCase(), actionMethod);
                }
                else if(actionMethodAnnotation.httpMethod().equals(ActionMethod.HttpMethod.POST)){
                    actionMap.put((POST_METHOD_PREFIX + actionName).toUpperCase(), actionMethod);
                }
                else {
                    actionMap.put((GET_METHOD_PREFIX + actionName).toUpperCase(), actionMethod);
                    actionMap.put((POST_METHOD_PREFIX + actionName).toUpperCase(), actionMethod);
                }
            }
            else {
                logger.warn("ActionMethod ignored, " + actionMethod);
            }
        }

        // register this to ControllerServlet
        String path = context.getModuleDir().getPath();
        moduleDirName = path.substring(path.lastIndexOf(File.separator) + 1);
        WebContextLoader.registerAction(moduleDirName, this);
    }

    public boolean preUnregister(ComponentContext context) {
        return true;
    }

    public void postUnregister() {
        WebContextLoader.removeAction(this);
    }

    public String getModuleName() {
        return context.getModuleName();
    }

    public String getModuleDirName() {
        return moduleDirName;
    }

    public String getName() {
        return context.getComponentId().getName();
    }

    public Logger getLogger() {
        return logger;
    }

    public final void execute(InvocationContext invocationContext) throws Exception {
        logger.info("Request accepted, URI: " + invocationContext.getRequestURI());
        long now = System.currentTimeMillis();
        Method actionMethod = getActionMethod(invocationContext);
        if (actionMethod == null) {
            throw new ServletException("No ActionMethod in Action Class: " + getClass().getName() + " responsable for " + (invocationContext.isPost() ? "POST" : "GET") + " action: " + getName() + "." + invocationContext.getActionMethodName() + " !");
        }
        ActionMethod actionMethodAnnotation = actionMethod.getAnnotation(ActionMethod.class);
        // invocation class
        Class<? extends Invocation> invocationClass = actionMethodAnnotation.invocationClass();
        String successView = actionMethodAnnotation.successView();
        // 没有设置 errorView，将会直接抛出异常
        String errorView = actionMethodAnnotation.errorView();

        //设置ActionMethod
        invocationContext.setActionMethod(actionMethod);
        //设置跳转方式
        invocationContext.getPageContext().setTargetMethod(actionMethodAnnotation.forwardMethod());
        //设置目标模板页面
        invocationContext.getPageContext().setTargetView(successView);

        Exception exception = null;
        try {
            //construct & verify invocation            
            initInvocation(invocationClass, invocationContext);
            // 设置通用PageContext属性
            initPageContext(invocationContext);
            checkSessionToken(invocationContext);
            // pre action
            preAction(invocationContext);
            // invoke action method
            actionMethod.invoke(this, invocationContext);
        }
        catch (InvocationException e) {
            //invocation exception, throw out
            doActionFailed(invocationContext);
            throw e; // throw out InvocationException
//            invocationContext.getPageContext().setTargetView(errorView);
        }
        catch (ValidateException e) {
            //invocation validate exception
            invocationContext.getPageContext().setTargetView(errorView);
            invocationContext.getPageContext().addValidateException(e);
            doActionFailed(invocationContext);
            exception = e;
        }
        catch (InvocationTargetException e) { // exception throwed, forward to error view
            Exception t = (Exception)e.getTargetException();
            logger.warn("Execute Action Method " + actionMethod.getName() + " throws exception.", t);
            invocationContext.getPageContext().setTargetView(errorView);
            invocationContext.getPageContext().setBusinessException(t);
            doActionFailed(invocationContext);
            exception = t;
        }
        catch (Exception e) { // exception throwed, forward to error view
            logger.warn("Execute Action Method " + actionMethod.getName() + " throws exception.", e);
            invocationContext.getPageContext().setTargetView(errorView);
            invocationContext.getPageContext().setBusinessException(e);
            doActionFailed(invocationContext);
            exception = e;
        }
        finally {
            postInitPageContext(invocationContext);
            postAction(invocationContext);
            releaseSessionToken(invocationContext);
        }
        
        logger.info("Request done, URI: " + invocationContext.getRequestURI() + ", consumed " + (System.currentTimeMillis()-now) + "ms.");
        // 没有设置 errorView, 抛出异常
        if (exception != null && (errorView == null || errorView.trim().length() == 0)) {
            throw exception;
        }
    }

    /**
     * 设置通用 PageContext 的 attribute
     * 业务设置的 attribute 不应该重名，否则会被通用 attribute 覆盖
     * @param invocationContext invocation context
     */
    protected void initPageContext(InvocationContext invocationContext){
        HttpServletRequest request = invocationContext.getServletRequest();
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("J_VALIDATE_EXCEPTIONS", pageContext.getValidateExceptions());
        pageContext.setAttribute("J_EXCEPTION", pageContext.getBusinessException());

        pageContext.setAttribute("J_SESSION_CONTEXT", invocationContext.getSessionContext());
        pageContext.setAttribute("J_PAGE_CONTEXT", pageContext);
        pageContext.setAttribute("J_REQUEST", request);
        //用于在页面上显示 vm 文件全路径，便于调试
        pageContext.setAttribute("J_WEBAPP_CONTEXT_PATH", request.getContextPath());
        pageContext.setAttribute("J_REQUEST_URI", request.getRequestURI());
        // request token，用来防止重复提交
        pageContext.setAttribute("J_REQUEST_TOKEN", System.currentTimeMillis() + "");
    }

    protected void postInitPageContext(InvocationContext invocationContext){
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute(PAGE_VIEW_PATH, pageContext.getTargeView());
    }


    private Method getActionMethod(InvocationContext invocationContext) {
        //决定调用 doGetXXX or doPostXXX
        Method actionMethod;
        String name = invocationContext.getActionMethodName();
        if (invocationContext.isPost()) {
            actionMethod = actionMap.get((POST_METHOD_PREFIX + name).toUpperCase());
        }
        else {
            actionMethod = actionMap.get((GET_METHOD_PREFIX + name).toUpperCase());
        }
        return actionMethod;
    }

    /**
     * do something before invoke action method
     *
     * @param invocationContext invcation context
     */
    protected void preAction(InvocationContext invocationContext) {
        
    }

    /**
     * do something after invocation action method
     *
     * @param invocationContext invocation context
     */
    protected void postAction(InvocationContext invocationContext) {

    }

    /**
     * 在 execute 过程中出现异常时，将回调该方法。
     * 该方法可以做一些补偿性工作，比如：在 buildInvocation 过程中出现了异常，
     * 可以通过实现该方法恢复数据，以便能够在 errorView中显示用户数据
     *
     * @param invocationContext invocationContext
     */
    protected void doActionFailed(InvocationContext invocationContext) {

    }

    /**
     * build Invocation
     * validator input
     * set invocation field value
     *
     * @param invocationClass   incation class
     * @param invocationContext invcation context
     * @throws InvocationException throw when contruct invocation failed
     * @throws ValidateException throw when invocation attribute validate failed
     */
    protected Invocation initInvocation(Class<? extends Invocation> invocationClass, InvocationContext invocationContext) throws InvocationException, ValidateException {
        Invocation invocation;
        if (invocationClass.equals(Invocation.class)) {
            invocation = new Invocation();
            invocation.putAll(invocationContext.getParameterMap());
        }
        else {
            try {
                invocation = invocationClass.newInstance();
                invocation.putAll(invocationContext.getParameterMap());
            }
            catch (Exception e) {
                throw new InvocationException("Construct invocation exception.", e);
            }
        }
        invocationContext.setInvocation(invocation);

        // verify & build form input field
        ValidateException validateException = null;
        for (Map.Entry<String, String[]> entry : invocationContext.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            try {
                Field field = ClassUtils.getDecaredField(invocationClass, key);
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Annotation[] validatorAnnotations = field.getAnnotations();
                if (fieldType.isArray()) {
                    Class<?> arrayType = fieldType.getComponentType();
                    Object[] params = (Object[])Array.newInstance(arrayType, values.length);
                    for (int i = 0; i < params.length; i++) {
                        if (validatorAnnotations.length > 0) {
                            for (Annotation validation : validatorAnnotations) {
                                try {
                                    // valiate field input and construct
                                    params[i] = Validators.validate(field, values[i], validation);
                                }
                                catch (ValidateException e) {
                                    // 只记录第一个 ValidateException
                                    if (validateException == null) {
                                        validateException = e;
                                    }
                                }
                            }
                        }
                        else {
                            //no validator, try to use ClassUtils construct object
                            params[i] = ClassUtils.newObject(arrayType, values[i]);
                        }
                    }
                    field.set(invocation, params);
                }
                else {
                    String value = values[0];
                    Object v = null;

                    if (validatorAnnotations.length > 0) {
                        for (Annotation validation : validatorAnnotations) {
                            try {
                                v = Validators.validate(field, value, validation);
                            }
                            catch (ValidateException e) {
                                // 只记录第一个 ValidateException
                                if (validateException == null) {
                                    validateException = e;
                                }
                            }
                        }
                    }
                    else {
                        v = ClassUtils.newObject(fieldType, value);
                    }
                    field.set(invocation, v);
                }
            }
            catch (NoSuchFieldException e) {
                //仅仅发出一个信息
                String msg = "Set invocation " + invocationClass.getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed, No such filed!";
                logger.info(msg);
//                    throw new InvocationException(msg, e);
            }
            catch (Throwable t) {
                String msg = "Set invocation + " + invocationClass.getName() + "'s field \"" + key + "\" with value " + Arrays.toString(values) + " failed!";
                logger.warn(msg, t);
                throw new InvocationException(msg, t);
            }
        }

        if (validateException != null) {
            String msg = "Set invocation + " + invocationClass.getName() + "'s field \"" + validateException.getInputField() + "\" with value \"" + validateException.getInputValue() + "\" failed, " + validateException.getMessage();
            logger.warn(msg);
            throw validateException; // throw exception to execute()
        }

        // build upload file field
        for (Map.Entry<String, FileUploaded> entry : invocationContext.getFileUploadedMap().entrySet()) {
            String key = entry.getKey();
            FileUploaded fileUploaded = entry.getValue();
            try {
                Field field = ClassUtils.getDecaredField(invocationClass, key);
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (FileUploaded.class.isAssignableFrom(fieldType)) {
                    field.set(invocation, fileUploaded);
                }
                else {
                    String msg = "Invocation " + invocationClass.getName() + " 's field " + field.getName() + " is not a type " + FileUploaded.class.getName();
                    logger.warn(msg);
                    throw new InvocationException(msg);
                }
            }
            catch (NoSuchFieldException e) {
                String msg = "Set invocation " + invocationClass.getName() + "'s FileUploaded field " + key + " with value " + fileUploaded + " failed!";
                logger.warn(msg, e);
                throw new InvocationException(msg, e);
            }
            catch (IllegalAccessException e) {
                String msg = "Set invocation " + invocationClass.getName() + "'s FileUploaded field " + key + " with value " + fileUploaded + " failed!";
                logger.warn(msg, e);
                throw new InvocationException(msg, e);
            }
        }

        /**
         * 有些需要关联验证的，比如：校验两次输入的密码是否正确，
         * 因为不能保证校验密码在初试密码之后得到校验，所以必须放到 validateAll 中进行校验
         */
        invocation.validateAll();
        return invocation;
    }

    protected void checkSessionToken(InvocationContext invocationContext) {
        Invocation invocation = invocationContext.getInvocation();
        if(invocation.getRequestToken() != null) {
            SessionContext sessionContext = invocationContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + invocation.getRequestToken();
            if(sessionContext.containsAttribute(key)) {
                // token 已经存在，是重复提交
                throw new ActionResubmitException("Detected re-submit, action: " + invocationContext.getFullActionMethodName() + ", token: " + invocation.getRequestToken());
            }
            else {
                sessionContext.setAttribute(key, "1");
            }
        }
    }

    protected void releaseSessionToken(InvocationContext invocationContext) {
        Invocation invocation = invocationContext.getInvocation();
        if(invocation.getRequestToken() != null) {
            SessionContext sessionContext = invocationContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + invocation.getRequestToken();
            sessionContext.removeAttribute(key);
        }
    }

    public static void main(String[] args) {

    }
}
