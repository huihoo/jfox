package net.sourceforge.jfox.framework.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.jfox.framework.ComponentId;

/**
 * 用来存储所有的 ComponentMeta
 * 所有的 ComponentMeta用一个 Map 保存，便于全局搜索，
 * 同时每个Module都有自己的CompoentRepo对象，便于按 Module 隔离
 * 
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class Repository {

    private final static Map<ComponentId, ComponentMeta> componentMap = new ConcurrentHashMap<ComponentId, ComponentMeta>();

    private Module module;

    private Repository(Module module) {
        this.module = module;
    }

    static synchronized Repository getModuleComponentRepo(Module module){
        return new Repository(module);
    }

    void addComponentMeta(ComponentMeta meta) {
        componentMap.put(meta.getComponentId(),meta);
    }

    ComponentMeta getComponentMeta(ComponentId id) throws ComponentNotFoundException, ComponentNotExportedException {
        if(!componentMap.containsKey(id)){
            throw new ComponentNotFoundException(id.toString());
        }
        ComponentMeta meta = componentMap.get(id);
        if(meta.getModule() != module && !meta.isExported()){
            throw new ComponentNotExportedException(id.toString());
        }
        return meta;
    }

    void removeComponentMeta(ComponentId id) throws ComponentNotFoundException {
        if(!componentMap.containsKey(id)){
            throw new ComponentNotFoundException(id.toString());
        }
        ComponentMeta meta = componentMap.get(id);
        if(meta.getModule() == module && !meta.isExported()){
            componentMap.remove(id);
        }
    }

    boolean hasComponentMeta(ComponentId id){
        return componentMap.containsKey(id);
    }

    /**
     * 返回该模块所有的 ComponentMeta
     * 返回的List已经按 Priority 排序
     */
    List<ComponentMeta> getModuleComponentMetas(){
        List<ComponentMeta> metas = new ArrayList<ComponentMeta>();
        for(ComponentMeta meta : componentMap.values()){
            if(meta.getModule() == this.module) {
                metas.add(meta);
            }
        }
        Collections.sort(metas);
        return metas;
    }

    List<ComponentMeta> getModuleComponentMetas(String moduleName){
        List<ComponentMeta> metas = new ArrayList<ComponentMeta>();
        for(ComponentMeta meta : componentMap.values()){
            if(meta.getModule().getName().equals(moduleName)) {
                metas.add(meta);
            }
        }
        Collections.sort(metas);
        return metas;
    }


    public static void main(String[] args) {

    }
}
