/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.manager.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;

import org.jfox.ejb3.EJBBucket;
import org.jfox.ejb3.EJBContainer;
import org.jfox.entity.EntityManagerFactoryBuilder;
import org.jfox.entity.EntityManagerFactoryBuilderImpl;
import org.jfox.entity.EntityManagerFactoryImpl;
import org.jfox.framework.Constants;
import org.jfox.framework.Framework;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.Module;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.WebContextLoader;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.validate.StringValidation;
import org.jfox.util.SystemUtils;

/**
 *
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "console")
public class WebConsoleAction extends ActionSupport {

    @ActionMethod(successView = "console/sysinfo.vhtml")
    public void doGetSysinfo(InvocationContext invocationContext) throws Exception {
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("jfoxVersion", Constants.VERSION);
        pageContext.setAttribute("webServerVersion", invocationContext.getServletContext().getServerInfo());
        pageContext.setAttribute("jvmVersion", SystemUtils.JAVA_VERSION);
        pageContext.setAttribute("jvmVendor", SystemUtils.JAVA_VENDOR);
        pageContext.setAttribute("osName", SystemUtils.OS_NAME);
        pageContext.setAttribute("osVersion", SystemUtils.OS_VERSION);
        pageContext.setAttribute("osArch", SystemUtils.OS_ARCH);
        pageContext.setAttribute("maxMemory", Runtime.getRuntime().maxMemory()/(1024*1024));
        pageContext.setAttribute("usedMemory", (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024));
    }
    
    @ActionMethod(successView = "console/jndi.vhtml")
    public void doGetJNDI(InvocationContext invocationContext) throws Exception{
        NamingEnumeration<Binding> enu = getEJBContainer().getNamingContext().listBindings("");
        PageContext pageContext = invocationContext.getPageContext();
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

    @ActionMethod(successView = "console/container.vhtml")
    public void doGetContainer(InvocationContext invocationContext) throws Exception{
        PageContext pageContext = invocationContext.getPageContext();
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

    @ActionMethod(successView = "console/jpa.vhtml")
    public void doGetJPA(InvocationContext invocationContext) throws Exception{
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

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("entityManagerFactories", entityManagerFactories);
        pageContext.setAttribute("namedSQLTemplates", EntityManagerFactoryBuilderImpl.getNamedSQLTemplates());
//        pageContext.setAttribute("caches", caches);
  
    }

    @ActionMethod(successView = "console/module.vhtml")
    public void doGetModules(InvocationContext invocationContext) throws Exception{
        Framework framework = WebContextLoader.getManagedFramework();
        Module systemModule = framework.getSystemModule();
        List<Module> allModules = framework.getAllModules();

        List<Module> modules = new ArrayList<Module>(allModules.size()+1);
        modules.add(systemModule);
        modules.addAll(allModules);
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("modules", modules);
    }

    @ActionMethod(successView = "console/testconnectionresult.vhtml", errorView = "console/testconnectionresult.vhtml",invocationClass = TestConnectionInvocation.class)
    public void doGetTestConnection(InvocationContext invocationContext) throws Exception {
        TestConnectionInvocation invocation = (TestConnectionInvocation)invocationContext.getInvocation();
        String unitName = invocation.getUnitName();
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("unitName", unitName);
        EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName).checkConnection();
    }

    @ActionMethod(successView = "console.jpa.do", targetMethod = ActionMethod.TargetMethod.REDIRECT, invocationClass = TestConnectionInvocation.class)
    public void doGetClearCacheConfig(InvocationContext invocationContext) throws Exception {
        TestConnectionInvocation invocation = (TestConnectionInvocation)invocationContext.getInvocation();
        String unitName = invocation.getUnitName();
        EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName(unitName).clearCache();
    }

    @ActionMethod(successView = "console/jpa.vhtml",invocationClass = TestConnectionInvocation.class)
    public void doGetClearCache(InvocationContext invocationContext) throws Exception {
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
