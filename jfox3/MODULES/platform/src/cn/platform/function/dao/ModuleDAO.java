package cn.iservicedesk.function.dao;

import java.util.List;

import cn.iservicedesk.function.entity.Module;
import cn.iservicedesk.infrastructure.DataAccessObject;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface ModuleDAO extends DataAccessObject {

    Module getModuleById(long id);

    void insertModule(Module module);

    List<Module> getAllModules();
}
