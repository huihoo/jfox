package net.sourceforge.jfox.framework.component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.sourceforge.jfox.framework.ComponentId;
import net.sourceforge.jfox.framework.Constants;
import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.event.ComponentUnloadedEvent;
import net.sourceforge.jfox.framework.event.ComponentLoadedEvent;
import net.sourceforge.jfox.framework.event.ModuleLoadingEvent;
import net.sourceforge.jfox.framework.event.ModuleLoadedEvent;
import net.sourceforge.jfox.framework.event.ModuleUnloadedEvent;
import net.sourceforge.jfox.util.FileFilterUtils;
import net.sourceforge.jfox.util.FileUtils;
import net.sourceforge.jfox.util.PlaceholderUtils;
import net.sourceforge.jfox.util.XMLUtils;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Module implements Comparable<Module> {

    protected final Logger logger = Logger.getLogger(this.getClass());

    protected Framework framework;

    /**
     * 模块的文件，或者目录
     */
    protected File moduleDir;

    private URL descriptorURL;

    /**
     * 模块名称
     */
    private String name;

    private String description;

    private int priority = 50;
    /**
     * 该模块需要引用的模块
     */
    private String[] refModules;

    protected ModuleClassLoader classLoader;

    private Repository repo = Repository.getModuleComponentRepo(this);

    public static enum STATUS {
        RESOLVED, STARTED, STOPPED
    }

    public Module(Framework framework, File file) throws ModuleResolvedFailedException {
        this.framework = framework;
        this.moduleDir = file;
        this.classLoader = initModuleClassLoader();
        this.resolve(); // 仅仅解析 xml，并不load相关的类和component
    }

    protected ModuleClassLoader initModuleClassLoader() {
        return new ModuleClassLoader(this);
    }

    public Framework getFramework() {
        return framework;
    }

    public File getModuleDir() {
        return moduleDir;
    }

    public ModuleClassLoader getModuleClassLoader() {
        return classLoader;
    }

    public ComponentMeta loadComponent(Class<? extends Component> implementataionClass) throws ComponentResolvedFailedException {
        ComponentMeta meta = new ComponentMeta(this, implementataionClass);
        registerComponent(meta);
        getFramework().getEventManager().fireComponentEvent(new ComponentLoadedEvent(meta.getComponentId()));
        return meta;
    }

    /**
     * 卸载一个Component
     *
     * @param id component id
     * @throws ComponentNotFoundException not found component
     */
    public void unloadComponent(ComponentId id) throws ComponentNotFoundException {
        logger.info("Unload component: " + id);
        if (isComponentLoaded(id)) {
            try {
                ComponentMeta meta = repo.getComponentMeta(id);
                // 会从 Module 中删除，并回调 preUnregister postUnregister
                meta.unload();
                getFramework().getEventManager().fireComponentEvent(new ComponentUnloadedEvent(id));
            }
            catch (ComponentNotExportedException e) {
                throw new ComponentNotFoundException("Failed to unload other module's component.", e);
            }
        }
        else {
            throw new ComponentNotFoundException(id.toString());
        }
    }

    /**
     * reload a component
     *
     * @param implementationClass component implementation class
     */
    public ComponentMeta reloadComponent(Class<? extends Component> implementationClass) {
        return null;
    }

    private void registerComponent(ComponentMeta meta) {
//        componentMetas.put(meta.getComponentId(), meta);
        repo.addComponentMeta(meta);
    }

    /**
     * 由 ComponentMeta 回调
     *
     * @param id component id
     * @throws ComponentNotFoundException if component not found
     */
    void unregisterComponent(ComponentId id) throws ComponentNotFoundException {
//        componentMetas.remove(id);
        repo.removeComponentMeta(id);
    }

    /**
     * 获得模块配置文件
     *
     * @return xml descriptor URL
     */
    public URL getDescriptorURL() {
        if (descriptorURL == null) {
            try {
                descriptorURL = new File(getModuleDir(), Constants.MODULE_CONFIG_DIR + "/" + Constants.MODULE_CONFIG_FILENAME).toURI().toURL();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // 不用 ClassLoader.getResource，会造成无法释放资源，而是 Web Application undeploy 失败
//            descriptorURL = getModuleClassLoader().getResource(Constants.MODULE_CONFIG_FILENAME);
        }
        return descriptorURL;
    }

    /**
     * 获取所有要加入到 MODULE classpath 的 url
     *
     * @return Module classpath urls
     */
    public URL[] getClasspathURLs() {
        List<URL> classpathURLs = new ArrayList<URL>();

        File classesPath = new File(moduleDir, Constants.MOUDULE_CLASS_OUTPUT_PATH);
        File webClassesPath = new File(moduleDir, Constants.MOUDULE_CLASS_WEB_OUTPUT_PATH);

        File configPath = new File(moduleDir, Constants.MODULE_CONFIG_DIR);

        File libPath = new File(moduleDir, "lib");
        File webLibPath = new File(moduleDir, "WEB-INF/lib");
        try {
            if (classesPath.exists()) {
                classpathURLs.add(classesPath.toURI().toURL()); // add to top
            }
            if (webClassesPath.exists()) {
                classpathURLs.add(webClassesPath.toURI().toURL()); // add to top
            }
            if (configPath.exists()) {
                classpathURLs.add(configPath.toURI().toURL()); // add to top
            }

            if (libPath.exists()) {
                List<File> jars = FileUtils.listFiles(libPath, FileFilterUtils.suffixFileFilter("jar", "zip"));
                for (File jar : jars) {
                    classpathURLs.add(jar.toURI().toURL());
                }
            }
            if (webLibPath.exists()) {
                List<File> jars = FileUtils.listFiles(webLibPath, FileFilterUtils.suffixFileFilter("jar", "zip"));
                for (File jar : jars) {
                    classpathURLs.add(jar.toURI().toURL());
                }
            }
        }
        catch (MalformedURLException e) {
            logger.warn("Malformed URL!", e);
        }
        return classpathURLs.toArray(new URL[classpathURLs.size()]);
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    protected void setPriority(int priority) {
        this.priority = priority;
    }

    public String[] getRefModules() {
        return refModules;
    }

    /**
     * 初始化，only 装载配置文件，不解析
     *
     * @throws ModuleResolvedFailedException 解析失败，比如，XML不符合规范，抛出该异常
     */
    protected void resolve() throws ModuleResolvedFailedException {
        URL descriptorURL = getDescriptorURL();
        if (descriptorURL == null) {
            logger.warn("Could not find module XML configuration for Module " + getModuleDir().toString() + ", will use default config.");
            setName(getModuleDir().getName());
            setDescription(getModuleDir().toString());
        }
        else {
            logger.info("Resolving XML descriptor: " + descriptorURL);
            Document doc;
            try {
                // 替换占位符
                String xmlContent = PlaceholderUtils.getInstance().evaluate(descriptorURL);
                doc = XMLUtils.loadDocument(xmlContent);
            }
            catch (Exception e) {
                logger.error("Error to get XML Document of Module descriptor.", e);
                throw new ModuleResolvedFailedException("Error to get XML Document of Module descriptor.", e);
            }

            Element rootElement = doc.getDocumentElement();
            setName(XMLUtils.getChildElementValueByTagName(rootElement, "name"));
            setDescription(XMLUtils.getChildElementValueByTagName(rootElement, "description"));
            setPriority(Integer.parseInt(XMLUtils.getChildElementValueByTagName(rootElement, "priority")));
            String _refModules = XMLUtils.getChildElementValueByTagName(rootElement, "ref-modules");
            if (_refModules != null) {
                this.refModules = _refModules.split(",");
            }
            else {
                this.refModules = new String[0];
            }
        }
    }

    /**
     * 解析模块，export class, 装载组件
     *
     * @throws Exception any exception
     */
    public void start() throws Exception {
        logger.info("Starting module: " + getName());
        getFramework().getEventManager().fireModuleEvent(new ModuleLoadingEvent(this));
        Class[] deployComponents = getModuleClassLoader().findClassAnnotatedWith(Service.class);
        for (Class<?> componentClass : deployComponents) {
            if (componentClass.isInterface()
                    || !Modifier.isPublic(componentClass.getModifiers())
                    || Modifier.isAbstract(componentClass.getModifiers())) {
                logger.warn("Class " + componentClass.getName() + " is annotated with @" + Service.class.getSimpleName() + ", but not is not a public concrete class, ignored!");
                continue;
            }
            if (!Component.class.isAssignableFrom(componentClass)) {
                logger.warn("Class " + componentClass.getName() + " is annotated with @" + Service.class.getSimpleName() + ", but not implements interface " + Component.class.getName() + ", ignored!");
                continue;
            }
            ComponentMeta meta = loadComponent(componentClass.asSubclass(Component.class));
            logger.info("Component " + componentClass.getName() + " loaded with id: " + meta.getComponentId() + "!");
        }
        // 实例化 not lazy components
        instantiateActiveComponent();
        getFramework().getEventManager().fireModuleEvent(new ModuleLoadedEvent(this));
    }

    protected void instantiateActiveComponent() throws ComponentInstantiateException {
        List<ComponentMeta> metas = repo.getModuleComponentMetas();
//        Collections.sort(metas);
        for (ComponentMeta meta : metas) {
            // 立即实例化
            if (meta.isActive()) {
                meta.getComponentInstance();
            }
        }
    }

    public void stop() throws Exception {
//
    }

    public void destroy() throws Exception {

    }

    /**
     * 销毁整个模块, unload all components
     */
    public void unload() {
        List<ComponentMeta> metas = repo.getModuleComponentMetas();
//        Collections.sort(metas);
        Collections.reverse(metas);
        for (ComponentMeta meta : metas) {
            try {
                unloadComponent(meta.getComponentId());
            }
            catch (ComponentNotFoundException e) {
                logger.warn("Unload component: " + meta.getComponentId() + " failed.", e);
            }
        }
        getFramework().getEventManager().fireModuleEvent(new ModuleUnloadedEvent(this));
    }

    /**
     * 获得该模块内的 Component 实例
     *
     * @param componentId componentId
     * @throws ComponentNotFoundException    if not found the component or component instantiate failed
     * @throws ComponentNotExportedException if found component in other module, but it is not exported
     */
    public Component getComponent(ComponentId componentId) throws ComponentNotFoundException, ComponentNotExportedException {

        try {
            ComponentMeta componentMeta = repo.getComponentMeta(componentId);
            return componentMeta.getComponentInstance();
        }
        catch (ComponentInstantiateException e) {
            throw new ComponentNotFoundException("Can not get component, id=" + componentId, e);
        }
    }

    public Component getComponent(String componentId) throws ComponentNotFoundException, ComponentNotExportedException {
        return getComponent(new ComponentId(componentId));
    }

    ComponentMeta getComponentMeta(ComponentId componentId) throws ComponentNotFoundException, ComponentNotExportedException {
        return repo.getComponentMeta(componentId);
    }

    public boolean isComponentLoaded(ComponentId id) {
        return repo.hasComponentMeta(id);
    }

    /**
     * 组件是否对外发布，使用了 @Expose 描述的组件是可发布组件，以其接口发布出来
     *
     * @param id component id
     */
    public boolean isComponentExported(ComponentId id) {
        if (!isComponentLoaded(id)) {
            return false;
        }
        else {
            try {
                ComponentMeta meta = repo.getComponentMeta(id);
                return meta.isExported();
            }
            catch (ComponentNotExportedException e) {
                return false;
            }
            catch (ComponentNotFoundException e) {
                return false;
            }
        }
    }

    public List<ComponentMeta> getAllComponentMetas() {
        return repo.getModuleComponentMetas();
    }

    public <T extends Component> Collection<T> findComponentByInterface(Class<T> interfaceClass) {
        List<T> components = new ArrayList<T>();
        for (ComponentMeta meta : repo.getModuleComponentMetas()) {
            if (meta.isImplemented(interfaceClass)) {
                try {
                    components.add((T)meta.getComponentInstance());
                }
                catch (ComponentInstantiateException e) {
                    logger.warn("Component instantiate failed, id=" + meta.getComponentId(), e);
                }
            }
        }
        return Collections.unmodifiableCollection(components);
    }

    public <T extends Component> Collection<T> findComponentByInterface(Class<T> interfaceClass, String moduleName) {
        List<T> components = new ArrayList<T>();
        for (ComponentMeta meta : repo.getModuleComponentMetas(moduleName)) {
            if (meta.isImplemented(interfaceClass)) {
                try {
                    components.add((T)meta.getComponentInstance());
                }
                catch (ComponentInstantiateException e) {
                    logger.warn("Component instantiate failed, id=" + meta.getComponentId(), e);
                }
            }
        }
        return Collections.unmodifiableCollection(components);
    }


    public int compareTo(Module o) {
        int thisVal = this.getPriority();
        int anotherVal = o.getPriority();
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    public static void main(String[] args) {

    }
}
