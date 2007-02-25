package net.sourceforge.jfox.framework.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;

import net.sourceforge.jfox.framework.annotation.Constant;
import net.sourceforge.jfox.framework.annotation.Inject;
import net.sourceforge.jfox.framework.dependent.ConstantDependence;
import net.sourceforge.jfox.framework.dependent.FieldEJBDependence;
import net.sourceforge.jfox.framework.dependent.FieldResourceDependence;
import net.sourceforge.jfox.framework.dependent.InjectDependence;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.util.AnnotationUtils;
import org.apache.log4j.Logger;

/**
 * ComponentFactory 负责实例化 Component，并完成 Constructor/Properties 依赖注射
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentFactory {

    protected static Logger logger = Logger.getLogger(ComponentFactory.class);

    private ComponentContext componentContext = null;

    private Constructor<? extends Component> constructor = null;

    // 是否正在实例化，避免循环调用
    private volatile boolean instantiating = false;

    private List<InjectDependence> injectDependences = new ArrayList<InjectDependence>();
    private List<ConstantDependence> constantDependences = new ArrayList<ConstantDependence>();
    private List<FieldEJBDependence> ejbDependences = new ArrayList<FieldEJBDependence>();
    private List<FieldResourceDependence> resourceDependences = new ArrayList<FieldResourceDependence>();

    public ComponentFactory(ComponentContext componentContext, Constructor<? extends Component> constructor) {
        this.componentContext = componentContext;
        this.constructor = constructor;

//        List<Field> fields = new ArrayList<Field>();
        // 找到需要注射的方法
        for (Field filed : (AnnotationUtils.getAnnotatedFields(getImplementationClass(), Inject.class))) {
            InjectDependence injectDependence = new InjectDependence(componentContext, filed);
            injectDependences.add(injectDependence);
        }
        for (Field filed : AnnotationUtils.getAnnotatedFields(getImplementationClass(), Constant.class)) {
            ConstantDependence constantDependence = new ConstantDependence(componentContext, filed);
            constantDependences.add(constantDependence);
        }

        for (Field filed : AnnotationUtils.getAnnotatedFields(getImplementationClass(), EJB.class)) {
            FieldEJBDependence fieldEJBDependence = new FieldEJBDependence(componentContext, filed);
            ejbDependences.add(fieldEJBDependence);
        }
        for (Field filed : AnnotationUtils.getAnnotatedFields(getImplementationClass(), Resource.class)) {
            FieldResourceDependence fieldResourceDependence = new FieldResourceDependence(componentContext, filed);
            resourceDependences.add(fieldResourceDependence);
        }
    }

    /**
     * 获得组件的 Class
     */
    public Class<? extends Component> getImplementationClass() {
        return constructor.getDeclaringClass();
    }

    protected Constructor<? extends Component> getConstructor() throws ComponentInstantiateException {
        return constructor;
    }

    /**
     * 生成一个组件实例
     *
     * @return Component instance
     * @throws ComponentInstantiateException throws if failed to instantiate component
     */
    public Component makeComponent() throws ComponentInstantiateException {
        logger.info("Starting to construct component: " + getImplementationClass());

        if (instantiating) {
            throw new ComponentInstantiateException("Cycle depencies in component " + getImplementationClass().getName());
        }
        instantiating = true;

        try {
            Constructor<? extends Component> constructor = getConstructor();
            Component component = instantiateComponent(constructor);
            postInstantiate(component, componentContext); // 回调 postInstantiate

            // inject @Contant
            for (ConstantDependence constantDependence : constantDependences) {
                try {
                    constantDependence.inject(component);
                }
                catch (InjectionException e) {
                    throw new ComponentInstantiateException("@Constant injection failed.", e);
                }
            }

            // inject @Inject
            for (InjectDependence injectDependence : injectDependences) {
                try {

                    injectDependence.inject(component);
                }
                catch (InjectionException e) {
                    throw new ComponentInstantiateException("@Inject injection failed.", e);
                }
            }

            // inject @EJB
            for (FieldEJBDependence ejbDependence : ejbDependences) {
                try {
                    ejbDependence.inject(component);
                }
                catch (InjectionException e) {
                    throw new ComponentInstantiateException("@EJB injection failed.", e);
                }
            }

            // inject @Resource
            for (FieldResourceDependence resourceDependence : resourceDependences) {
                try {
                    resourceDependence.inject(component);
                }
                catch (InjectionException e) {
                    throw new ComponentInstantiateException("@Resource injection failed.", e);
                }
            }
            postPropertiesSet(component);

            return component;

        }
        finally {
            instantiating = false;
            logger.debug("Component: " + getImplementationClass() + " construct finished!");
        }
    }


    /**
     * 实现具体构造组件实例的过程
     *
     * @param constructor 构造器
     * @return Component实现
     * @throws ComponentInstantiateException if failed to instantiate component
     */
    protected Component instantiateComponent(Constructor<? extends Component> constructor) throws ComponentInstantiateException {
        try {
            return constructor.newInstance();
        }
        catch (Exception e) {
            throw new ComponentInstantiateException("Component Instantiate failed with exception " + getImplementationClass().getName(), e);
        }
    }

    protected void postInstantiate(Component component, ComponentContext componentContext) {
        if (component instanceof InstantiatedComponent) {
            ((InstantiatedComponent)component).instantiated(componentContext);
        }
    }

    protected void postPropertiesSet(Component component) {
        if (component instanceof InstantiatedComponent) {
            ((InstantiatedComponent)component).postPropertiesSet();
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
