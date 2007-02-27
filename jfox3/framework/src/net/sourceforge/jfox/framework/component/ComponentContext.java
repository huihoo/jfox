package net.sourceforge.jfox.framework.component;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jfox.framework.BaseException;
import net.sourceforge.jfox.framework.BaseRuntimeException;
import net.sourceforge.jfox.framework.ComponentId;
import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.framework.event.ComponentEvent;

/**
 * Component 上下文
 * ComponentContext 和 Component 是一对一的关系
 * ComponentContex 和 ComponentMeta 不一定是一对一的关系
 * 对于Non Singleton Component，一个 ComponentMeta可能对应多个Component，也就对应多个ComponentContex
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentContext {

    //注意: 这里不能保存Component的实例，而应该每次从Module中取得，这样才能实现热部署
    //private Component concreteComponent;

    private Framework framework;

    private String moduleName;

    private ComponentId componentId;

    /**
     * 用户属性
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    ComponentContext(Framework framework, String module, ComponentId componentId) {
        this.framework = framework;
        this.moduleName = module;
        this.componentId = componentId;
    }

    private Module getModule() {
        return framework.getModule(getModuleName());
    }

    public String getModuleName(){
        return moduleName;
    }

    public File getModuleDir(){
        return getModule().getModuleDir();
    }

    public Component getMyselfComponent() {
        try {
            return getComponentById(componentId);
        }
        catch(BaseException e){
            throw new BaseRuntimeException("Can not get Myself Component!", e);
        }
    }

    public ComponentId getComponentId() {
        return componentId;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public Iterator<String> attributeKeys() {
        return attributes.keySet().iterator();
    }

    public Object hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public ExtentionPoint getExtentionPoint(String pointId) {
        try {
            return getModule().getComponentMeta(componentId).getExtentionPoint(pointId);
        }
        catch(ComponentNotFoundException e) {
            return null;
        }
        catch(ComponentNotExportedException e) {
            return null;
        }
    }

    /**
     * Component 通过 ComponentContext 可以获得需要的 Component 引用
     *
     * @param componentId component Id
     * @throws ComponentNotFoundException e
     * @throws ComponentNotExportedException e
     */
    public Component getComponentById(ComponentId componentId) throws ComponentNotFoundException, ComponentNotExportedException{
        return getModule().getComponent(componentId);
    }

    /**
     * find components by the interface/super class provided
     * 注意：仅在本 Module 中查找
     * 
     * @param interfaceClass interface class
     */
    public Component[] findComponentBySuper(Class interfaceClass) {
        return getModule().findComponentByInterface(interfaceClass);
    }

    public Component[] findComponentBySuper(Class interfaceClass, String moduleName) {
        return getModule().findComponentByInterface(interfaceClass, moduleName);
    }

    public void fireComponentEvent(ComponentEvent componentEvent){
        getModule().getFramework().getListenerManager().fireComponentEvent(componentEvent);
    }

    public static void main(String[] args) {

    }
    
}
