package net.sourceforge.jfox.mvc;

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

import net.sourceforge.jfox.framework.component.ActiveComponent;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.ComponentUnregistration;
import net.sourceforge.jfox.framework.component.InstantiatedComponent;
import net.sourceforge.jfox.framework.component.SingletonComponent;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;
import net.sourceforge.jfox.mvc.validate.ValidateException;
import net.sourceforge.jfox.mvc.validate.Validators;
import net.sourceforge.jfox.util.AnnotationUtils;
import net.sourceforge.jfox.util.ClassUtils;
import org.apache.log4j.Logger;

/**
 * Action super class
 * <p/>
 * 子类实现 doGetXXX doPostXXX 方法，如 public void doGetIndex(InvocationContext invocationContext) throws Exception
 * XXX 是不区分大小写，所以不能有同名的 XXX 方法
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public abstract class ActionSupport implements Action, InstantiatedComponent, ComponentUnregistration, ActiveComponent, SingletonComponent {

    /**
     * post action prefix, invoked method will be "DOPOST + %ACTION_NAME%"
     */
    public static final String POST_METHOD_PREFIX = "doPost";
    public static final String GET_METHOD_PREFIX = "doGet";

    private String moduleDirName;

    private ComponentContext context;

    /**
     * 声明的 ActionMethod
     * action method name => Method
     */
    private Map<String, Method> actionMap = new ConcurrentHashMap<String, Method>();

    protected Logger logger = Logger.getLogger(this.getClass());

    public void instantiated(ComponentContext componentContext) {
        this.context = componentContext;
    }

    public void postPropertiesSet() {
        Method[] actionMethods = AnnotationUtils.getAnnotatedMethods(this.getClass(), ActionMethod.class);
        for (Method actionMethod : actionMethods) {
            if (actionMethod.getReturnType().equals(void.class)
                    && actionMethod.getParameterTypes().length == 1
                    && actionMethod.getParameterTypes()[0].equals(InvocationContext.class)) {
                // 统一转换成大写
                String name = actionMethod.getName().toUpperCase();
                actionMap.put(name, actionMethod);
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

    public void preUnregister(ComponentContext context) {
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

        Method actionMethod = getActionMethod(invocationContext);
        if (actionMethod == null) {
            throw new ServletException("No such action: " + getName() + "." + invocationContext.getActionName());
        }
        invocationContext.setActionMethod(actionMethod);
        Class<?> invocationClass = actionMethod.getAnnotation(ActionMethod.class).invocationClass();

        ActionMethod actionMethodAnnotation = actionMethod.getAnnotation(ActionMethod.class);
        String successView = actionMethodAnnotation.successView();
        String errorView = actionMethodAnnotation.errorView();
        invocationContext.getPageContext().setTargetView(successView);

        if (!Invocation.class.isAssignableFrom(invocationClass)) {
            throw new InvocationException("Invalid invocation class." + invocationClass.getName());
        }

        Invocation invocation;
        if (invocationClass.equals(Invocation.class)) {
            invocation = new Invocation();
            invocation.putAll(invocationContext.getParameterMap());
        }
        else {
            try {
                invocation = (Invocation)invocationClass.newInstance();
                invocation.putAll(invocationContext.getParameterMap());
            }
            catch (Exception e) {
                throw new InvocationException("Construct invocation exception.", e);
            }
        }

        invocationContext.setInvocation(invocation);
        //build & set invocation
        try {
            buildInvocation(invocationClass, invocationContext);
            invocationContext.setInvocation(invocation);
            preAction(invocationContext);
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
        }
        catch (InvocationTargetException e) { // exception throwed, forward to error view
            Exception t = (Exception)e.getTargetException();
            logger.warn("Execute Action Method " + actionMethod.getName() + " throws exception.", t);
            invocationContext.getPageContext().setTargetView(errorView);
            invocationContext.getPageContext().setBusinessException(t);
            doActionFailed(invocationContext);
        }
        catch (Exception e) { // exception throwed, forward to error view
            logger.warn("Execute Action Method " + actionMethod.getName() + " throws exception.", e);
            invocationContext.getPageContext().setTargetView(errorView);
            invocationContext.getPageContext().setBusinessException(e);
            doActionFailed(invocationContext);
        }

        postAction(invocationContext);
    }

    private Method getActionMethod(InvocationContext invocationContext) {
        //决定调用 doGetXXX or doPostXXX
        Method actionMethod;
        String name = invocationContext.getActionName();
        if (invocationContext.isPostMethod()) {
            actionMethod = actionMap.get((POST_METHOD_PREFIX + name).toUpperCase());
        }
        else {
            actionMethod = actionMap.get((GET_METHOD_PREFIX + name).toUpperCase());
        }
        return actionMethod;
    }

    /**
     * 在 execute 过程中出现异常时，将回调该方法
     * 该方法可以做一些补偿性工作，比如：在 buildInvocation 过程中出现了异常，
     * 可以通过实现该方法取得 errorView 的数据
     *
     * @param invocationContext invocationContext
     */
    protected void doActionFailed(InvocationContext invocationContext) {

    }

    /**
     * implement in sub class
     *
     * @param invocationContext invcation context
     */
    protected boolean preAction(InvocationContext invocationContext) {
        return true;
    }

    /**
     * implement in sub class
     *
     * @param invocationContext invocation context
     */
    protected void postAction(InvocationContext invocationContext) {

    }

    /**
     * build Invocation
     * validator input
     * set invocation field value
     *
     * @param invocationClass   incation class
     * @param invocationContext invcation context
     * @throws InvocationException e
     * @throws ValidateException   e
     */
    protected Invocation buildInvocation(Class invocationClass, InvocationContext invocationContext) throws InvocationException, ValidateException {
        Invocation invocation = invocationContext.getInvocation();
        // build form input field
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

    public static void main(String[] args) {

    }
}
