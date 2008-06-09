package org.jfox.mvc;

import org.apache.log4j.Logger;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.component.Module;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.dependent.FieldEJBDependence;
import org.jfox.mvc.dependent.FieldInjectDependence;
import org.jfox.mvc.dependent.FieldResourceDependence;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.ClassUtils;

import javax.annotation.Resource;
import javax.ejb.EJB;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jun 5, 2008 6:07:05 PM
 */
public class ActionBucket {
    /**
     * post action prefix, invoked method will be "DOPOST + %ACTION_NAME%"
     */
    public static final String POST_METHOD_PREFIX = "POST_";
    public static final String GET_METHOD_PREFIX = "GET_";

    protected final Logger logger = Logger.getLogger(this.getClass());

    private ActionContainer actionContainer;
    private Module module;
    private Class<?> actionClass;

    private Object actionObject;

    private String actionName;
    private Map<String, Method> actionMethodMap = new HashMap<String, Method>();

    /**
     * Field级别的依赖，描述在 Field 上
     */
    protected List<FieldEJBDependence> fieldEJBdependents = new ArrayList<FieldEJBDependence>();
    protected List<FieldResourceDependence> fieldResourceDependents = new ArrayList<FieldResourceDependence>();
    protected List<FieldInjectDependence> fieldInjectionDependents = new ArrayList<FieldInjectDependence>();

    public ActionBucket(ActionContainer actionContainer, Class<?> actionClass, Module module) {
        this.actionContainer = actionContainer;
        this.actionClass = actionClass;
        this.module = module;

        org.jfox.mvc.annotation.Action actionAnnotation = actionClass.getAnnotation(org.jfox.mvc.annotation.Action.class);
        actionName = actionAnnotation.name();
        if(actionName.trim().equals("")) {
            actionName = this.actionClass.getSimpleName();
            if(actionName.endsWith("Action")) {
                actionName = actionName.substring(0, actionName.length() - "Action".length());
            }
        }

        introspectActionMethods();
        introspectFieldDependents();
    }

    public Class getActionClass() {
        return actionClass;
    }

    public Module getModule() {
        return module;
    }

    protected void introspectActionMethods() {
        Method[] actionMethods = AnnotationUtils.getAnnotatedMethods(getActionClass(), ActionMethod.class);
        Map<String, Method> actionMap = new HashMap<String, Method>(actionMethods.length);
        for (Method actionMethod : actionMethods) {
            if (actionMethod.getReturnType().equals(void.class)
                    && actionMethod.getParameterTypes().length == 1
                    && actionMethod.getParameterTypes()[0].equals(ActionContext.class)) {
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
        actionMethodMap.putAll(actionMap);
    }

    protected void introspectFieldDependents() {
        //需要发现 AllSuperClass
        List<Field> allEJBFields = new ArrayList<Field>();
        List<Field> allResourceFields = new ArrayList<Field>();

        List<Field> allInjectFields = new ArrayList<Field>();

        // getAllSuperClass，也包括了自已
        for (Class<?> clazz : ClassUtils.getAllSuperclasses(this.getActionClass())) {
            Field[] ejbFields = AnnotationUtils.getAnnotatedFields(clazz, EJB.class);
            allEJBFields.addAll(Arrays.asList(ejbFields));

            Field[] resourceFields = AnnotationUtils.getAnnotatedFields(clazz, Resource.class);
            allResourceFields.addAll(Arrays.asList(resourceFields));

            Field[] injectFields = AnnotationUtils.getAnnotatedFields(clazz, Inject.class);
            allInjectFields.addAll(Arrays.asList(injectFields));

        }

        for (Field field : allEJBFields) {
//            EJB ejb = field.getAnnotation(EJB.class);
            fieldEJBdependents.add(new FieldEJBDependence(this, field));
        }

        for (Field field : allResourceFields) {
//            Resource resource = field.getAnnotation(Resource.class);
            fieldResourceDependents.add(new FieldResourceDependence(this, field));
        }

        for(Field field : allInjectFields){
            fieldInjectionDependents.add(new FieldInjectDependence(this,field));
        }
    }

    public String getActionName() {
        return actionName;
    }

    public void start() {
        // 实例化，并完成依赖注入
        try {
            actionObject = makeObject();
        }
        catch (Exception e) {
            throw new ActionInstantiateException("Failed to instantiate Action: {Class=}" + getActionClass() + ", name=" + getActionName() + "}", e);
        }        
    }

    //--- jakarta commons-pool PoolableObjectFactory ---
    protected Object makeObject() throws Exception {
        Object actionObject = getActionClass().newInstance();

        // 注入 @Inject
        for (FieldInjectDependence fieldInjectDependence : fieldInjectionDependents) {
            fieldInjectDependence.inject(actionObject);
        }

        // 注入 @EJB
        for (FieldEJBDependence fieldEJBDependence : fieldEJBdependents) {
            fieldEJBDependence.inject(actionObject);
        }

        // 注入 @Resource
        for (FieldResourceDependence fieldResourceDependence : fieldResourceDependents) {
            fieldResourceDependence.inject(actionObject);
        }

        //返回 actionObject
        return actionObject;
    }


    public String toString() {
        return super.toString();
    }

    public Method getActionMethod(ActionContext actionContext) {
        //决定调用 doGetXXX or doPostXXX
        Method actionMethod;
        String name = actionContext.getActionMethodName();
        if (actionContext.isPost()) {
            actionMethod = actionMethodMap.get((POST_METHOD_PREFIX + name).toUpperCase());
        }
        else {
            actionMethod = actionMethodMap.get((GET_METHOD_PREFIX + name).toUpperCase());
        }
        return actionMethod;
    }

    public Object getActionObject() {
        return actionObject;
    }

    public static void main(String[] args) {

    }
}
