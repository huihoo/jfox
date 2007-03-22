package net.sourceforge.jfox.manager.console;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jfox.framework.Framework;
import net.sourceforge.jfox.framework.annotation.Service;
import net.sourceforge.jfox.framework.component.Module;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.WebContextLoader;
import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;

/**
 *
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Service(id = "console")
public class WebConsoleAction extends ActionSupport {

    @ActionMethod(successView = "console/console.vhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception {

    }

    //DataSource, NamedNativeQuery, PersistenceUnit
    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetJPAAction(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "modules.vhtml")
    public void doGetModulesAction(InvocationContext invocationContext) throws Exception{
        Framework framework = WebContextLoader.getManagedFramework();
        Module systemModule = framework.getSystemModule();
        List<Module> allModules = framework.getAllModules();

        List<Module> modules = new ArrayList<Module>(allModules.size()+1);
        modules.add(systemModule);
        modules.addAll(allModules);
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("modules", modules);
    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetJTAAction(InvocationContext invocationContext) throws Exception{
        Framework framework = WebContextLoader.getManagedFramework();
//        Component[] containers = framework.getSystemModule().findComponentByInterface(EJBContainer.class);
//        containers[0].
    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetJNDIAction(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(successView = "jpaview.vhtml")
    public void doGetEJBContainerAction(InvocationContext invocationContext) throws Exception{

    }

    public static void main(String[] args) {

    }
}
