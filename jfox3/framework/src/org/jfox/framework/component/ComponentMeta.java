/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import org.apache.log4j.Logger;
import org.jfox.framework.ComponentId;
import org.jfox.framework.annotation.Exported;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.event.ComponentListener;
import org.jfox.framework.event.FrameworkListener;
import org.jfox.framework.event.ModuleListener;
import org.jfox.framework.invoker.ComponentInvokerFactory;
import org.jfox.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComponentMeta 是组件的元数据对象，保留了组件所有的信息
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentMeta implements Comparable<ComponentMeta>{
    private final static Logger logger = Logger.getLogger(ComponentMeta.class);

    public final static boolean DEFAULT_SINGLETON = true;

    public final static boolean DEFAULT_TYPE_SINGLETON = false;

    public final static int DEFAULT_PRIORITY = 0;

    /**
     * Component 所在的 Module
     */
    private Module module;

    /**
     * 布署文件中描述的id
     */
    private String id;

    private boolean active = false;

    /**
     * 该组件的描述
     */
    protected String description = "";

    /**
     * 组件是否单例， true时Registry只会生成该组件的一个实例，
     * false时Registry为每次getComponentInstance调用生成一个实例， 默认为 true <br>
     */
    protected boolean singleton = DEFAULT_SINGLETON;

    /**
     * 优先级，仅表示在所处的模块中的优先级
     */
    protected int priority = DEFAULT_PRIORITY;

//    private Class<? extends Component> interfaceClass;

    private List<Class> interfaceClasses = new ArrayList<Class>();
    private Class<? extends Component> implementationClass;

//    private boolean exported = false;

    /**
     * 布署之后的 ComponentId
     */
    private ComponentId componentId;

    /**
     * 该组件定义的扩展点
     */
    private Map<String, ExtentionPoint> extentionPointMap = new HashMap<String, ExtentionPoint>();

    /**
     * 该 Component 引用 的Component
     */
    private List<ComponentMeta> referencedComponent = new ArrayList<ComponentMeta>();

    /**
     * 引用了该 Component 的 Component
     */
    private List<ComponentMeta> beReferencedComponent = new ArrayList<ComponentMeta>();

    private ComponentFactory componentFactory;

    /**
     * 该Component的实例
     * 对于 Singleton Component，只会有一个实例
     * 对于非 Singlton Component，保存最后一个实例
     */
    private Component lastConcreteComponent;

    private ComponentContext componentContext;

    public ComponentMeta(Module module, Class<? extends Component> implementationClass) throws ComponentResolvedFailedException {
        this.module = module;
        this.implementationClass = implementationClass;
        this.resolve();
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Module getModule() {
        return module;
    }

    /**
     * componentId = id@%MODULE_NAME%
     *
     * @return ComponentId
     */
    public ComponentId getComponentId() {
        return componentId;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public int getPriority() {
        return priority;
    }

    protected void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isSingleton() {
        return singleton;
    }

    protected void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

/*
    public boolean isExported() {
        return exported;
    }

    protected void setExported(boolean exported) {
        this.exported = exported;
    }

*/

    public Class[] getInterfaceClass() {
        return interfaceClasses.toArray(new Class[interfaceClasses.size()]);
    }

    public boolean isImplemented(Class<?> interfaceClass) {
        return interfaceClass.isAssignableFrom(getImplementationClass());
    }

    public Class<? extends Component> getImplementationClass() {
        return implementationClass;
    }

    protected void setImplementationClass(Class<? extends Component> implementationClass) {
        this.implementationClass = implementationClass;
    }

    public ExtentionPoint getExtentionPoint(String pointId) {
        return extentionPointMap.get(pointId);
    }

    protected void addExtentionPoint(ExtentionPoint extentionPoint) {
        extentionPointMap.put(extentionPoint.getId(), extentionPoint);
    }

    public synchronized Component getComponentInstance() throws ComponentInstantiateException {
        logger.debug("Get component instance, componentId is: " + getComponentId());
        if (lastConcreteComponent == null) {

            // 这里保存的是 Component 真实对象
            lastConcreteComponent = componentFactory.makeComponent();
            // register listeners
            registerListeners(lastConcreteComponent);

        }

        //根据Component的类型创建正确的ComponentInvoker
        //通过调用 ComponentRefFactory 返回动态代理对象
        return ProxyReferenceFactory.createProxyComponent(
                getModule().getFramework(),
                getModule().getName(),
                getComponentId(),
                getInterfaceClass(),
                ComponentInvokerFactory.getComponentInvoker(ComponentInvokerFactory.TYPE.Reflect));
    }

    /**
     * 得到Component的实体对象
     */
    Component getConcreteComponent() {
        return lastConcreteComponent;
    }

    /**
     * 解析 该Component 关联的 XML
     *
     * @throws ComponentResolvedFailedException
     *          if failed to resolve Component Descriptor
     */
    private void resolve() throws ComponentResolvedFailedException {
        Service serviceAnnotation = implementationClass.getAnnotation(Service.class);
        if (serviceAnnotation == null) {
            throw new ComponentResolvedFailedException("Component " + implementationClass.getName() + " not annotated with " + Service.class.getName());
        }
        String id = serviceAnnotation.id();
        if (id != null && id.trim().length() > 0) {
            setId(serviceAnnotation.id());
        }
        else {
            setId(implementationClass.getSimpleName());
        }
        setActive(serviceAnnotation.active());
        if(ActiveComponent.class.isAssignableFrom(implementationClass)) {
            setActive(true);
        }
        setSingleton(serviceAnnotation.singleton());
        if(SingletonComponent.class.isAssignableFrom(implementationClass)) {
            setSingleton(true);
        }

        setDescription(serviceAnnotation.description());
        setPriority(serviceAnnotation.priority());


        // 设置服务接口
        if (serviceAnnotation.interfaces().length > 0) { // Deployment annotation 指定了接口
            for (Class interfaceClass : serviceAnnotation.interfaces()) {
                // check 指定的 interface 是否合法
                if (!interfaceClass.isAssignableFrom(implementationClass)) {
                    throw new ComponentResolvedFailedException("Invalid interface class annotated to Component: " + implementationClass.getName());
                }
                interfaceClasses.add(interfaceClass);
            }
        }
        else { // 没有指定接口，则认为所有接口都可以服务
            Class[] allInterfaces = ClassUtils.getAllInterfaces(implementationClass);
            interfaceClasses.addAll(Arrays.asList(allInterfaces));
        }

        // 没有接口的 Component 不部署
        if(interfaceClasses.isEmpty()) {
            throw new ComponentResolvedFailedException("Component: " + getId() + " has no interface, refused!");
        }

        for (Class intf : interfaceClasses) {
            if (intf.isAnnotationPresent(Exported.class) && intf.getClassLoader() == this.getModule().getModuleClassLoader()) {
                /* export interface
                 * Exported interface 即使Module重新load，也不会重新装载
                 * 因为其他模块可能已经有了该interface class引用
                 */
//                getModule().getFramework().getClassLoader().addExportedClass(getModule().getName(), intf);
                // 是否可以对其它模块服务
//                setExported(true);
            }
        }
        this.componentId = new ComponentId(getId());

        Constructor<? extends Component> constructor;
        // 找到实例化时使用的构造器

        try {
            constructor = implementationClass.getConstructor();
        }
        catch(NoSuchMethodException e) {
            throw new ComponentResolvedFailedException("Can not find default constructor for Component: " + implementationClass.getName());
        }

        //创建CompnentContext
        componentContext = new ComponentContext(getModule().getFramework(), getModule().getName(), getComponentId());

        // 创建 ComponentFactory
        componentFactory = new ComponentFactory(componentContext, constructor);
    }

    /**
     * 销毁 ComponentMeta
     */
    boolean unload() {
        logger.debug("Unload componentMeta: " + getComponentId());

        Component instance = getConcreteComponent();

        if(instance == null) {
            return true;
        }

        // callback ComponentUnregistation.preUnregister
        boolean preUnregisterSuccess = true;
        if (instance instanceof ComponentUnregistration) {
            preUnregisterSuccess = ((ComponentUnregistration)instance).preUnregister(componentContext);
        }

        if(!preUnregisterSuccess) {
            logger.warn("Unload component: " + getComponentId() + " failed, because preUnregister return false.");
            return false;
        }

        unregisterListeners();
        // remove frame component meta cache
        try {
            getModule().unregisterComponent(getComponentId());
        }
        catch(ComponentNotFoundException e){
            logger.warn("Unload with Exception.", e);
        }

        // callback ComponentUnregistation.postUnregister
        if (instance instanceof ComponentUnregistration) {
            ((ComponentUnregistration)instance).postUnregister();
        }

        // 销毁创建的对象
        this.lastConcreteComponent = null;
        this.componentContext = null;
        referencedComponent.clear();
        beReferencedComponent.clear();
        return true;
    }

    private void registerListeners(Component theComponent) {
        if(theComponent instanceof FrameworkListener) {
            getModule().getFramework().getEventManager().registerFrameworkListener((FrameworkListener)theComponent);
        }
        if(theComponent instanceof ModuleListener) {
            getModule().getFramework().getEventManager().registerModuleListener((ModuleListener)theComponent);
        }
        if(theComponent instanceof ComponentListener) {
            getModule().getFramework().getEventManager().registerComponentListener((ComponentListener)theComponent);
        }

    }

    private void unregisterListeners(){
        //unregister listeners
        Component instance = getConcreteComponent();

        if(instance == null) return;

        if(instance instanceof FrameworkListener) {
            getModule().getFramework().getEventManager().unregisterFrameworkListener((FrameworkListener)instance);
        }
        if(instance instanceof ModuleListener) {
            getModule().getFramework().getEventManager().unregisterModuleListener((ModuleListener)instance);
        }
        if(instance instanceof ComponentListener) {
            getModule().getFramework().getEventManager().unregisterComponentListener((ComponentListener)instance);
        }

    }

    public int compareTo(ComponentMeta o) {
        int thisVal = this.getPriority();
        int anotherVal = o.getPriority();
        return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
    }

    public String toString() {
        return "ComponentMeta{ID=" + getComponentId() + ", MODULE=" + getModule() + "}";
    }

    public static void main(String[] args) throws Exception {

    }
}
