package jfox.platform.function.dao;

import java.util.List;

import jfox.platform.function.entity.Module;
import jfox.platform.infrastructure.DataAccessObject;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface ModuleDAO extends DataAccessObject {

    Module getModuleById(long id);

    void insertModule(Module module);

    List<Module> getAllModules();
}
