/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import org.jfox.framework.ComponentId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用来存储所有的 ComponentMeta
 * 所有的 ComponentMeta用一个 Map 保存，便于全局搜索，
 * 同时每个Module都有自己的CompoentRepo对象，便于按 Module 隔离
 * 
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Repository {

    private final static Map<ComponentId, ComponentMeta> componentMetas = new ConcurrentHashMap<ComponentId, ComponentMeta>();

    private final static Repository instance = new Repository();

    private Repository() {

    }

    public static Repository getInstance(){
        return instance;
    }

    void addComponentMeta(ComponentMeta meta) {
        componentMetas.put(meta.getComponentId(),meta);
    }

    public void removeComponentMeta(ComponentId id) throws ComponentNotFoundException {
        if(!componentMetas.containsKey(id)){
            throw new ComponentNotFoundException(id.toString());
        }
        componentMetas.remove(id);
    }

    public boolean hasComponentMeta(ComponentId id){
        return componentMetas.containsKey(id);
    }

    public List<ComponentMeta> getComponentMetas(String moduleName){
        List<ComponentMeta> metas = new ArrayList<ComponentMeta>();
        for(ComponentMeta meta : componentMetas.values()){
            if(meta.getModule().getName().equals(moduleName)) {
                metas.add(meta);
            }
        }
        Collections.sort(metas);
        return metas;
    }

    public List<ComponentMeta> getComponentMetas(){
        return Collections.unmodifiableList(new ArrayList<ComponentMeta>(componentMetas.values()));
    }

    public ComponentMeta getComponentMeta(ComponentId id) throws ComponentNotFoundException {
        if(!componentMetas.containsKey(id)){
            throw new ComponentNotFoundException(id.toString());
        }
        ComponentMeta meta = componentMetas.get(id);
        return meta;
    }

    /**
     * 获得该模块内的 Component 实例
     *
     * @param componentId componentId
     * @throws ComponentNotFoundException    if not found the component or component instantiate failed
     * @throws ComponentNotExportedException if found component in other module, but it is not exported
     */
    public Component getComponent(ComponentId componentId) throws ComponentNotFoundException, ComponentInstantiateException {
            ComponentMeta componentMeta = componentMetas.get(componentId);
            return componentMeta.getComponentInstance();
    }

    public Component getComponent(String componentId) throws ComponentNotFoundException, ComponentInstantiateException {
        return getComponent(new ComponentId(componentId));
    }

    public boolean isComponentLoaded(ComponentId id) {
        return this.hasComponentMeta(id);
    }

/*
    public Component getComponent(ComponentId componentId, String module) throws ComponentNotFoundException, ComponentNotExportedException {

    }

    public Component getComponent(String componentId, String module) throws ComponentNotFoundException, ComponentNotExportedException {
        
    }

    public boolean isComponentLoaded(ComponentId id, String module) {
        
    }
*/

    public static void main(String[] args) {

    }
}
