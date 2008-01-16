/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
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
     * 保存 invocationClass 到其 Filed/Annotation的映射
     */
    private Map<Class<? extends Invocation>, Map<String, FieldValidation>> invocationMap = new HashMap<Class<? extends Invocation>, Map<String, FieldValidation>>();

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
                // 分析 actionMethod 对应的 @ActionMethod，并根据 @ActionMethod支持的 HttpMethod 作为 key
                ActionMethod actionMethodAnnotation = actionMethod.getAnnotation(ActionMethod.class);
                String actionMethodName = actionMethodAnnotation.name().trim(); // ActionMethod 指定的名称
                if(actionMethodName.length() == 0) { // 判断是否为空
                    logger.error("ActionMethod name can not be empty. Action: " + this.getClass().getName() + "." + actionMethod.getName());
                    continue;
                }

                if (actionMethodAnnotation.httpMethod().equals(ActionMethod.HttpMethod.GET)) {
                    String key = (GET_METHOD_PREFIX + actionMethodName).toUpperCase();
                    //判断 action key 是否已经存在，如果存在，日志并忽略
                    if(actionMap.containsKey(key)) {
                        logger.warn("ActionMethod with name " + actionMethodName + " in Action's Method " + this.getClass().getName() + "." + actionMethod.getName() + " has been registed, ignored.");
                    }
                    else {
                        actionMap.put(key, actionMethod);
                    }
                }
                else if (actionMethodAnnotation.httpMethod().equals(ActionMethod.HttpMethod.POST)) {
                    String key = (POST_METHOD_PREFIX + actionMethodName).toUpperCase();
                    if(actionMap.containsKey(key)) {
                        logger.warn("ActionMethod with name " + actionMethodName + " in Action's Method " + this.getClass().getName() + "." + actionMethod.getName() + " has been registed, ignored.");
                    }
                    else {
                        actionMap.put(key, actionMethod);
                    }
                }
                else {
                    String key1 = (GET_METHOD_PREFIX + actionMethodName).toUpperCase();
                    String key2 = (POST_METHOD_PREFIX + actionMethodName).toUpperCase();

                    if(actionMap.containsKey(key1) || actionMap.containsKey(key2)){
                        logger.warn("ActionMethod with name " + actionMethodName + " in Action's Method " + this.getClass().getName() + "." + actionMethod.getName() + " has been registed, ignored.");
                    }
                    else {
                        actionMap.put((GET_METHOD_PREFIX + actionMethodName).toUpperCase(), actionMethod);
                        actionMap.put((POST_METHOD_PREFIX + actionMethodName).toUpperCase(), actionMethod);
                    }
                }
            }
            else {
                logger.warn("ActionMethod ignored, " + actionMethod);
            }
        }

        // register this to ControllerServlet
        String path = context.getModuleDir().getPath();
        moduleDirName = path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public boolean preUnregister(ComponentContext context) {
        return true;
    }

    public void postUnregister() {

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
        // 将把 InvocationContext 关联到 ThreadLocal
        invocationContext.initInvocationContext();
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
            preExecute(invocationContext);

            try {
                //construct & verify invocation
                initInvocation(invocationClass, invocationContext);
            }
            catch (InvocationException e) {
                //invocation exception, throw out
                throw e; // throw out InvocationException
//            invocationContext.getPageContext().setTargetView(errorView);
            }
            catch (ValidateException e) {
                //invocation validate exception
                invocationContext.getPageContext().setTargetView(errorView);
                invocationContext.getPageContext().addValidateException(e);
                exception = e;
            }

            postInitInvocation(invocationContext);

            if (exception == null) { // 初始化 invocation 没有异常
                try {
                    checkSessionToken(invocationContext);
                    // pre action
                    preAction(invocationContext);
                    // invoke action method
                    actionMethod.invoke(this, invocationContext);
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
                    try {
                        if (exception != null) {
                            doActionFailed(invocationContext);
                        }
                    }
                    finally {
                        postAction(invocationContext);
                        releaseSessionToken(invocationContext);
                    }
                }
            }
        }
        finally {
            //每次执行 Action 之后都应该解除 ThreadContext
            postExecute(invocationContext);
            invocationContext.disassociateThreadInvocationContext();
        }
        logger.info("Request done, URI: " + invocationContext.getRequestURI() + ", consumed " + (System.currentTimeMillis() - now) + "ms.");
        // 没有设置 errorView, 抛出异常
        if (exception != null && (errorView == null || errorView.trim().length() == 0)) {
            throw exception;
        }
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
     * do something before Execute
     *
     * @param invocationContext invcation context
     */
    protected void preExecute(InvocationContext invocationContext) {
        HttpServletRequest request = invocationContext.getServletRequest();
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("J_SESSION_CONTEXT", invocationContext.getSessionContext());
        pageContext.setAttribute("J_PAGE_CONTEXT", pageContext);
        pageContext.setAttribute("J_REQUEST", request);
        //用于在页面上显示 vm 文件全路径，便于调试
        pageContext.setAttribute("J_WEBAPP_CONTEXT_PATH", request.getContextPath());
        pageContext.setAttribute("J_REQUEST_URI", request.getRequestURI());

    }

    protected void postInitInvocation(InvocationContext invocationContext) {
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("J_VALIDATE_EXCEPTIONS", pageContext.getValidateExceptions());
    }

    /**
     * do something after Execute
     *
     * @param invocationContext invocation context
     */
    protected void postExecute(InvocationContext invocationContext) {

    }

    /**
     * do something before invoke action method
     *
     * @param invocationContext invcation context
     */
    protected void preAction(InvocationContext invocationContext) {
        PageContext pageContext = invocationContext.getPageContext();
        // request token，用来防止重复提交
        pageContext.setAttribute("J_REQUEST_TOKEN", System.currentTimeMillis() + "");
    }

    /**
     * do something after invocation action method
     *
     * @param invocationContext invocation context
     */
    protected void postAction(InvocationContext invocationContext) {
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute(PAGE_VIEW_PATH, pageContext.getTargeView());
        pageContext.setAttribute("J_EXCEPTION", pageContext.getBusinessException());
        pageContext.setAttribute("J_INVOCATION", invocationContext.getInvocation());
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
     * @throws ValidateException   throw when invocation attribute validate failed
     */
    protected Invocation initInvocation(Class<? extends Invocation> invocationClass, InvocationContext invocationContext) throws InvocationException, ValidateException {
        Invocation invocation;
        if (invocationClass.equals(Invocation.class)) {
            invocation = new Invocation() {
            };
            invocation.init(Collections.EMPTY_MAP, invocationContext.getParameterMap(), invocationContext.getFilesUploaded());
        }
        else {
            try {
                invocation = invocationClass.newInstance();
                // verify input then build fields
            }
            catch (Exception e) {
                throw new InvocationException("Construct invocation exception.", e);
            }
        }
        invocation.init(getInvocationFieldValidationMap(invocationClass), invocationContext.getParameterMap(), invocationContext.getFilesUploaded());
        /**
         * 有些需要关联验证的，比如：校验两次输入的密码是否正确，
         * 因为不能保证校验密码在初试密码之后得到校验，所以必须放到 validateAll 中进行校验
         */
        invocation.validateAll();
        invocationContext.setInvocation(invocation);
        return invocation;
    }

    private Map<String, FieldValidation> getInvocationFieldValidationMap(Class<? extends Invocation> invocationClass) {
        if (invocationMap.containsKey(invocationClass)) {
            return Collections.unmodifiableMap(invocationMap.get(invocationClass));
        }
        //构造 fieldMap & validationMap
        Field[] allFields = ClassUtils.getAllDecaredFields(invocationClass);
        Map<String, FieldValidation> fieldValidationMap = new HashMap<String, FieldValidation>(allFields.length);

        for (Field field : allFields) {
            if (!field.getDeclaringClass().equals(Invocation.class)) { //过滤掉Invocation自身的Field
                if (fieldValidationMap.containsKey(field.getName())) {
                    logger.warn("Reduplicate filed name: " + field.getName() + " in invocation: " + invocationClass.getName());
                    continue;
                }
                Annotation validationAnnotation = getAvailableValidationAnnotation(field);
                FieldValidation fieldValidation = new FieldValidation(field, validationAnnotation);
                fieldValidationMap.put(field.getName(), fieldValidation);
            }
        }
        invocationMap.put(invocationClass, fieldValidationMap);
        return Collections.unmodifiableMap(fieldValidationMap);
    }

    private Annotation getAvailableValidationAnnotation(Field field) {
        int count = 0;
        Annotation validAnnotation = null;
        Annotation[] fieldAnnotations = field.getAnnotations();
        for (Annotation annotation : fieldAnnotations) {
            if (Validators.isValidationAnnotation(annotation)) {
                validAnnotation = annotation;
                count++;
            }
        }
        if (count == 0) {
            return null;
        }
        else if (count > 1) {
            logger.warn("More than one Validation Annotation on " + field + ", will use last one.");
            return validAnnotation;
        }
        else {
            return validAnnotation;
        }
    }

    protected void checkSessionToken(InvocationContext invocationContext) {
        Invocation invocation = invocationContext.getInvocation();
        if (invocation.getRequestToken() != null) {
            SessionContext sessionContext = invocationContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + invocation.getRequestToken();
            if (sessionContext.containsAttribute(key)) {
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
        if (invocation != null && invocation.getRequestToken() != null) {
            SessionContext sessionContext = invocationContext.getSessionContext();
            String key = SessionContext.TOKEN_SESSION_KEY + invocation.getRequestToken();
            sessionContext.removeAttribute(key);
        }
    }

    static class FieldValidation {
        private Field field;
        private Annotation validationAnnotation;

        FieldValidation(Field field, Annotation validationAnnotation) {
            this.field = field;
            this.validationAnnotation = validationAnnotation;
        }

        public Field getField() {
            return field;
        }

        public Annotation getValidationAnnotation() {
            return validationAnnotation;
        }
    }

    public static void main(String[] args) {

    }
}
