/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.manager.console;

import org.jfox.ejb3.EJBBucket;
import org.jfox.ejb3.EJBContainer;
import org.jfox.entity.EntityManagerFactoryBuilder;
import org.jfox.entity.EntityManagerFactoryBuilderImpl;
import org.jfox.entity.EntityManagerFactoryImpl;
import org.jfox.framework.Constants;
import org.jfox.framework.Framework;
import org.jfox.framework.component.Module;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.WebContextLoader;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.validate.StringValidation;
import org.jfox.util.SystemUtils;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name = "console")
public class WebConsoleAction extends ActionSupport {

    @ActionMethod(name="sysinfo",successView = "console/sysinfo.vhtml")
    public void doGetSysinfo(ActionContext actionContext) throws Exception {
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("jfoxVersion", Constants.VERSION);
        pageContext.setAttribute("webServerVersion", actionContext.getServletContext().getServerInfo());
        pageContext.setAttribute("jvmVersion", SystemUtils.JAVA_VERSION);
        pageContext.setAttribute("jvmVendor", SystemUtils.JAVA_VENDOR);
        pageContext.setAttribute("osName", SystemUtils.OS_NAME);
        pageContext.setAttribute("osVersion", SystemUtils.OS_VERSION);
        pageContext.setAttribute("osArch", SystemUtils.OS_ARCH);
        pageContext.setAttribute("maxMemory", Runtime.getRuntime().maxMemory()/(1024*1024));
        pageContext.setAttribute("usedMemory", (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024));
    }
    
    @ActionMethod(name="jndi",successView = "console/jndi.vhtml")
    public void doGetJNDI(ActionContext actionContext) throws Exception{
        NamingEnumeration<Binding> enu = getEJBContainer().getNamingContext().listBindings("");
        PageContext pageContext = actionContext.getPageContext();
        List<Binding> bindings = new ArrayList<Binding>();
        while(enu.hasMoreElements()){
            bindings.add(enu.nextElement());
        }
        
        Collections.sort(bindings, new Comparator<Binding>(){
            public int compare(Binding o1, Binding o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        pageContext.setAttribute("bindings", bindings);
    }

    @ActionMethod(name="container",successView = "console/ejb.vhtml")
    public void doGetContainer(ActionContext actionContext) throws Exception{
        PageContext pageContext = actionContext.getPageContext();
        EJBContainer container = getEJBContainer();
        int defaultTransactionTimeout = container.getTransactionTimeout();
        List<EJBBucket> buckets = new ArrayList<EJBBucket>(container.listBuckets());
        Collections.sort(buckets, new Comparator<EJBBucket>(){
            public int compare(EJBBucket o1, EJBBucket o2) {
                if(o1.getModule().getName().equals(o2.getModule().getName())) {
                    return o1.getEJBName().compareTo(o2.getEJBName());
                }
                else {
                    return o1.getModule().getName().compareTo(o2.getModule().getName());
                }
            }
        });
        pageContext.setAttribute("defaultTransactionTimeout", defaultTransactionTimeout);
        pageContext.setAttribute("buckets", buckets);
    }

    @ActionMethod(name="jpa",successView = "console/jpa.vhtml")
    public void doGetJPA(ActionContext actionContext) throws Exception{
        //DataSource, NamedNativeQuery, PersistenceUnit
//        EntityManagerFactoryBuilder emfBuilder = getEntityManagerFactoryBuilder();
        Collection<EntityManagerFactoryImpl> entityManagerFactories = EntityManagerFactoryBuilderImpl.getEntityManagerFactories();

/*
        List<Cache> caches = new ArrayList<Cache>();
        for(EntityManagerFactoryImpl emfactory : entityManagerFactories){
            Collection<CacheConfig> cacheConfigs = emfactory.getCacheConfigs();
            for(CacheConfig cacheConfig : cacheConfigs){
                Collection<Cache> _caches = cacheConfig.getAllCaches();
                caches.addAll(_caches);
            }
        }
*/

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("entityManagerFactories", entityManagerFactories);
        pageContext.setAttribute("namedSQLTemplates", EntityManagerFactoryBuilderImpl.getNamedSQLTemplates());
//        pageContext.setAttribute("caches", caches);
  
    }

    @ActionMethod(name="modules",successView = "console/module.vhtml")
    public void doGetModules(ActionContext actionContext) throws Exception{
        Framework framework = WebContextLoader.getManagedFramework();
        Module systemModule = framework.getSystemModule();
        List<Module> allModules = framework.getAllModules();

        List<Module> modules = new ArrayList<Module>(allModules.size()+1);
        modules.add(systemModule);
        modules.addAll(allModules);
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("modules", modules);
    }

    @ActionMethod(name="testconnection",successView = "console/testconnectionresult.vhtml", errorView = "console/testconnectionresult.vhtml",invocationClass = TestConnectionInvocation.class)
    public void doGetTestConnection(ActionContext actionContext) throws Exception {
        TestConnectionInvocation invocation = (TestConnectionInvocation)actionContext.getInvocation();
        String unitName = invocation.getUnitName();
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("unitName", unitName);
        EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName).checkConnection();
    }

    @ActionMethod(name="clearcacheconfig",successView = "console.jpa.do", forwardMethod = ActionMethod.ForwardMethod.REDIRECT, invocationClass = TestConnectionInvocation.class)
    public void doGetClearCacheConfig(ActionContext actionContext) throws Exception {
        TestConnectionInvocation invocation = (TestConnectionInvocation)actionContext.getInvocation();
        String unitName = invocation.getUnitName();
        EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName).clearCache();
    }

    @ActionMethod(name="clearcache",successView = "console/jpa.vhtml",invocationClass = TestConnectionInvocation.class)
    public void doGetClearCache(ActionContext actionContext) throws Exception {
/*
        TestConnectionInvocation invocation = (TestConnectionInvocation)invocationContext.getInvocation();
        String unitName = invocation.getUnitName();
        EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName).clearCache();
        doGetJPA(invocationContext);
*/
    }


    private EJBContainer getEJBContainer(){
        Framework framework = WebContextLoader.getManagedFramework();
        Collection<EJBContainer> containers = framework.getSystemModule().findComponentByInterface(EJBContainer.class);
        return containers.iterator().next();
    }

    private EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(){
        Framework framework = WebContextLoader.getManagedFramework();
        Collection<EntityManagerFactoryBuilder> entityManagerFactoryBuilders = framework.getSystemModule().findComponentByInterface(EntityManagerFactoryBuilder.class);
        return entityManagerFactoryBuilders.iterator().next();
    }

    public static class TestConnectionInvocation extends Invocation {
        @StringValidation(nullable = false)
        private String unitName;

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }

    public static void main(String[] args) {

    }
}
