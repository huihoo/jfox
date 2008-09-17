/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import org.apache.log4j.Logger;
import org.jfox.framework.ComponentId;
import org.jfox.framework.Constants;
import org.jfox.framework.Framework;
import org.jfox.framework.FrameworkClassLoader;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.event.ComponentLoadedEvent;
import org.jfox.framework.event.ComponentUnloadedEvent;
import org.jfox.framework.event.ModuleLoadedEvent;
import org.jfox.framework.event.ModuleUnloadedEvent;
import org.jfox.util.FileFilterUtils;
import org.jfox.util.FileUtils;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Module {

    public final static String CLASS_DIR = "classes";
    public final static String WEB_CLASS_DIR ="WEB-INF/classes";
    public final static String LIB_DIR = "lib";
    public final static String WEB_LIB_DIR ="WEB-INF/lib";
    public final static String VIEWS_DIR ="views";

    protected final Logger logger = Logger.getLogger(this.getClass());

    protected Framework framework;

    /**
     * 模块的文件，或者目录
     */
    protected File moduleDir;

    /**
     * 模块名称
     */
    private String name;

    private final Repository repo = Repository.getInstance();

    public static enum STATUS {
        RESOLVED, LOADED, UNLOADED
    }

    public Module(Framework framework, File file) throws ModuleResolvedFailedException {
        this.framework = framework;
        this.moduleDir = file;
    }

    public Framework getFramework() {
        return framework;
    }

    public File getModuleDir() {
        return moduleDir;
    }

    public FrameworkClassLoader getModuleClassLoader() {
        return (FrameworkClassLoader)framework.getClassLoader();
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
    public boolean unloadComponent(ComponentId id) throws ComponentNotFoundException {
        logger.info("Unload component: " + id + ", Module: " + getName());
        boolean unloadSuccess = false;
        if (isComponentLoaded(id)) {
            ComponentMeta meta = repo.getComponentMeta(id);
            // 会从 Module 中删除，并回调 preUnregister postUnregister
            unloadSuccess = meta.unload();
            getFramework().getEventManager().fireComponentEvent(new ComponentUnloadedEvent(id));
            return unloadSuccess;
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
        if(repo.hasComponentMeta(meta.getComponentId())) {
            throw new ComponentExistedException(meta.toString());
        }
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

    /**
     * 解析模块，export class, 装载组件
     *
     * @throws Exception any exception
     */
    public void init() throws Exception {
        logger.info("Starting module: " + getName());
        if(!isSystemModule()) {
            getModuleClassLoader().addURLs(getClasspathURLs());
        }
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
        //需要明确预加载的Component，以便能监听 ModuleLoadingEvent
        preActiveComponent();
        // 实例化 not lazy components
        instantiateActiveComponent();
        getFramework().getEventManager().fireModuleEvent(new ModuleLoadedEvent(this));
    }

    protected boolean isSystemModule(){
        return false;
    }

    protected void preActiveComponent(){

    }

    protected void instantiateActiveComponent() throws ComponentInstantiateException {
        Collection<ComponentMeta> metas = repo.getComponentMetas();
//        Collections.sort(metas);
        for (ComponentMeta meta : metas) {
            // 立即实例化
            if (meta.isActive()) {
                meta.getComponentInstance();
            }
        }
    }

    public void destroy() throws Exception {
        List<ComponentMeta> metas = repo.getComponentMetas(this.getName());
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
     */
/*
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
*/

    public Collection<ComponentMeta> getAllComponentMetas() {
        return repo.getComponentMetas(this.getName());
    }

    public <T extends Component> Collection<T> findComponentsByInterface(Class<T> interfaceClass) {
        List<T> matchedComponents = new ArrayList<T>();
        for (ComponentMeta meta : repo.getComponentMetas()) {
            if (meta.isImplemented(interfaceClass)) {
                try {
                    matchedComponents.add((T)meta.getComponentInstance());
                }
                catch (ComponentInstantiateException e) {
                    logger.warn("Component instantiate failed, id=" + meta.getComponentId(), e);
                }
            }
        }
        return Collections.unmodifiableCollection(matchedComponents);
    }

    public <T extends Component> Collection<T> findComponentsByInterface(Class<T> interfaceClass, String moduleName) {
        List<T> components = new ArrayList<T>();
        for (ComponentMeta meta : repo.getComponentMetas(moduleName)) {
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

    public String toString() {
        return "Module: " + getName();
    }

    public static void main(String[] args) {

    }
}
