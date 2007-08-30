package jfox.platform.function.bo;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import jfox.platform.function.dao.ModuleDAO;
import jfox.platform.function.entity.Module;
import jfox.platform.infrastructure.DataAccessObject;
import jfox.platform.infrastructure.SuperBO;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Stateless
@Local
public class ModuleBOBean extends SuperBO implements ModuleBO{

    @EJB
    ModuleDAO moduleDAO;

    public DataAccessObject getDataAccessObject() {
        return moduleDAO;
    }

    public Module getModuleById(long moduleId) {
        return moduleDAO.getModuleById(moduleId);
    }

    public void newModule(Module module) {
        moduleDAO.insertModule(module);
    }

    public List<Module> getAllModules() {
        return moduleDAO.getAllModules();
    }
}
