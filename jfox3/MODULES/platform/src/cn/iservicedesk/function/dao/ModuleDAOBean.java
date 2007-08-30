package cn.iservicedesk.function.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

import cn.iservicedesk.function.entity.Module;
import cn.iservicedesk.infrastructure.SuperDAO;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Stateless
@Local
@NamedNativeQueries(
        {
        @NamedNativeQuery(
                name = ModuleDAOBean.GET_MODOULE_BY_ID,
                query = "SELECT * FROM T_FUNC_MODULE WHERE ID=$ID",
                resultClass = Module.class
        ),
        @NamedNativeQuery(
                name = ModuleDAOBean.GET_ALL_MODOULES,
                query = "SELECT * FROM T_FUNC_MODULE WHERE VSTATUS=0",
                resultClass = Module.class
        ),
        @NamedNativeQuery(
                name = ModuleDAOBean.INSERT_MODULE,
                query = "INSERT INTO T_FUNC_MODULE(" +
                        "ID," +
                        "NAME, " +
                        "LOCAL_NAME, " +
                        "PRIORITY, " +
                        "CREATE_TIME, " +
                        "CREATOR, " +
                        "LAST_MODIFIED, " +
                        "LAST_MODIFIER, " +
                        "VSTATUS, " +
                        "DESCRIPTION, " +
                        "REFS, " +
                        "BIND_ACTION, " +
                        "ICON) " +
                        "VALUES (" +
                        "$MODULE.getId()," +
                        "$MODULE.getName()," +
                        "$MODULE.getLocalName()," +
                        "$MODULE.getPriority()," +
                        "$MODULE.getCreateTime()," +
                        "$MODULE.getCreator()," +
                        "$MODULE.getLastModified()," +
                        "$MODULE.getLastModifier()," +
                        "$MODULE.getVstatus()," +
                        "$MODULE.getDescription()," +
                        "$MODULE.getRefs()," +
                        "$MODULE.getBindAction()," +
                        "$MODULE.getIcon())"
        )
                }
)
public class ModuleDAOBean extends SuperDAO implements ModuleDAO {

    public static final String GET_MODOULE_BY_ID = "getModuleById";
    public static final String GET_ALL_MODOULES = "getAllModules";
    public final static String INSERT_MODULE = "insertModule";

    public Module getModuleById(long id) {
        return (Module)getEntityObject(GET_MODOULE_BY_ID, "ID", id);
    }

    public void insertModule(Module module) {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("MODULE", module);
        executeNamedNativeUpdate(INSERT_MODULE, params);
    }

    public List<Module> getAllModules() {
        return (List<Module>)processNamedNativeQuery(GET_ALL_MODOULES, null);
    }
}
