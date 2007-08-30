package jfox.platform.function.bo;

import java.util.List;

import jfox.platform.function.entity.Module;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ModuleBO {

    Module getModuleById(long moduleId);

    void newModule(Module module);

    List<Module> getAllModules();
}
