/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import org.apache.log4j.Logger;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.jfox.ejb3.naming.JNDIContextHelper;
import org.jfox.ejb3.transaction.JTATransactionManager;
import org.jfox.entity.cache.CacheConfig;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ASMClassLoader;
import org.jfox.framework.component.ActiveComponent;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.component.ComponentUnregistration;
import org.jfox.framework.component.Module;
import org.jfox.framework.event.ModuleEvent;
import org.jfox.framework.event.ModuleListener;
import org.jfox.framework.event.ModuleLoadedEvent;
import org.jfox.framework.event.ModuleUnloadedEvent;
import org.jfox.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceException;
import javax.persistence.QueryHint;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "EntityManagerFactoryBuilder", active = true, singleton = true, priority = Integer.MIN_VALUE)
public class EntityManagerFactoryBuilderImpl implements EntityManagerFactoryBuilder, Component, ComponentInitialization, ComponentUnregistration, ModuleListener, ActiveComponent {

    protected static Logger logger = Logger.getLogger(EntityManagerFactoryBuilderImpl.class);

    private final static String PERSISTENCE_CONFIG_FILE = "META-INF/persistence.xml";

    //TransactionManager
    private TransactionManager transactionManager = null;

    /**
     * Entity Manager Factory Map
     * unit name => EntityManagerFactoryImpl
     */
    private static Map<String, EntityManagerFactoryImpl> emFactoryMap = new HashMap<String, EntityManagerFactoryImpl>();
    /**
     * query name => query template
     */
    private static Map<String, NamedSQLTemplate> namedSQLTemplates = new HashMap<String, NamedSQLTemplate>();

    private Document xmlDocument = null;

    private static boolean inited = false;
    private boolean containerManaged = true;

    private EntityTransaction entityTransaction = null;

    public static final String DEFAULT_UNITNAME = "default";
    public static final String QUERY_HINT_KEY_FORM_CAHCE_PREFIX = "cache.";
    public static final String QUERY_HINT_KEY_FOR_JDBC_COMPATIBLE = "jdbc.compatible";

    public EntityManagerFactoryBuilderImpl() {

    }

    /**
     * 容器外运行时，通过 Persistence.createEntityManagerFactory 调用时，会使用该方法
     * 需要初始化 EntityManagerFactoryBuilderImpl，注册所有 NamedQuery
     *
     * @param name unit name
     */
    public static EntityManagerFactoryImpl getEntityManagerFactoryByName(String name) {
        if (!inited) { // 没有初始化，是容器外调用，如果是容器内调用，应该已经初始化
            EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = new EntityManagerFactoryBuilderImpl();
            entityManagerFactoryBuilder.containerManaged = false;
            // 初始化所有的 EntityManagerFactory
            entityManagerFactoryBuilder.initEntityManagerFactories();
            ASMClassLoader asmClassLoader = new ASMClassLoader(entityManagerFactoryBuilder.getClass().getClassLoader());
            Set<Class> namedQueryClasses = new HashSet<Class>();
            namedQueryClasses.addAll(Arrays.asList(asmClassLoader.findClassAnnotatedWith(NamedNativeQueries.class)));
            namedQueryClasses.addAll(Arrays.asList(asmClassLoader.findClassAnnotatedWith(NamedNativeQuery.class)));
            entityManagerFactoryBuilder.registerNamedQueriesByClasses(namedQueryClasses.toArray(new Class[namedQueryClasses.size()]));
            inited = true;
        }
        return emFactoryMap.get(name);
    }

    public static EntityManagerFactory getDefaultEntityManagerFactory() {
        if (emFactoryMap.size() != 1) {
            throw new PersistenceException("More than one unitName, can not decide default!");
        }
        return emFactoryMap.values().toArray(new EntityManagerFactory[1])[0];
    }

    public static Collection<EntityManagerFactoryImpl> getEntityManagerFactories() {
        return Collections.unmodifiableCollection(emFactoryMap.values());
    }

    /**
     * 使用 @Resource 未指定 name 注入
     */
    public static DataSource getDefaultDataSource() {
        return ((EntityManagerFactoryImpl)getDefaultEntityManagerFactory()).getDataSource();
    }

    /**
     * 使用 @Resource 指定 name 注入
     *
     * @param unitName unit name, same as @resource name
     */
    public static DataSource getDataSourceByUnitName(String unitName) {
        EntityManagerFactoryImpl emf = emFactoryMap.get(unitName);
        if (emf == null) {
            throw new PersistenceException("Can not find DataSource with unitName: " + unitName);
        }
        else {
            return emf.getDataSource();
        }
    }

    /**
     * get data source by Mapped Name, if inject by @PersistenceContext(mappedName="")
     *
     * @param mappedName mapped name, same as jndi name
     */
    public static DataSource getDataSourceByMappedName(String mappedName) {
        for (EntityManagerFactory emf : emFactoryMap.values()) {
            StandardXAPoolDataSource dataSource = (StandardXAPoolDataSource)(((EntityManagerFactoryImpl)emf).getDataSource());
            if ((dataSource.getDataSourceName().equals(mappedName))) {
                return dataSource;
            }
        }
        throw new PersistenceException("Can not find DataSource with mappedName: " + mappedName);
    }

    public static Collection<NamedSQLTemplate> getNamedSQLTemplates() {
        return Collections.unmodifiableCollection(namedSQLTemplates.values());
    }

    //get CacheConfig by unit & cacheConfigName
    public static CacheConfig getCacheConfig(String unitName) {
        EntityManagerFactoryImpl emf = getEntityManagerFactoryByName(unitName);
        if (emf != null) {
            return emf.getCacheConfig();
        }
        else {
            return null;
        }
    }

    public void postContruct(ComponentContext componentContext) {
        inited = true;
    }

    public void postInject() {
        containerManaged = true;
        initEntityManagerFactories();
    }

    public void postUnregister() {

        for (EntityManagerFactory emFactory : emFactoryMap.values()) {
            // will close data source
            emFactory.close();
        }
        emFactoryMap.clear();
        namedSQLTemplates.clear();
    }

    public boolean preUnregister(ComponentContext context) {
        return true;
    }

    public boolean isContainerManaged() {
        return containerManaged;
    }

    public void moduleChanged(ModuleEvent moduleEvent) {

        if (moduleEvent instanceof ModuleLoadedEvent) {
            Module module = moduleEvent.getModule();
            Class[] namedQueriesClasses = module.getModuleClassLoader().findClassAnnotatedWith(NamedNativeQueries.class);
            registerNamedQueriesByClasses(namedQueriesClasses);
        }

        if (moduleEvent instanceof ModuleUnloadedEvent) {
            Module module = moduleEvent.getModule();
            Iterator<Map.Entry<String, NamedSQLTemplate>> it = namedSQLTemplates.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, NamedSQLTemplate> entry = it.next();
                NamedSQLTemplate sqlTemplate = entry.getValue();
                // 注销所在模块的 NamedSQLTemplate
                if (sqlTemplate.getDefinedClass().getClassLoader() == module.getModuleClassLoader()) {
                    logger.info("Unregister Named Query defined in class: " + sqlTemplate.getDefinedClass().getName() + ", template SQL: " + sqlTemplate.getTemplateSQL());
                    it.remove();
                }
            }
        }

    }

    private void initEntityManagerFactories() {
        URL url = this.getClass().getClassLoader().getResource(PERSISTENCE_CONFIG_FILE);
        if (url == null) {
            logger.warn("Can not found persistence config file: " + PERSISTENCE_CONFIG_FILE);
            return;
        }
        logger.info("Initializing EntityManagers use: " + PERSISTENCE_CONFIG_FILE);
        transactionManager = JTATransactionManager.getIntsance();
        // 初始化 EntityTransaction
        entityTransaction = new EntityTransactionImpl(transactionManager);

        try {
            xmlDocument = XMLUtils.loadDocument(url);
            Element rootElement = xmlDocument.getDocumentElement();
            List<Element> persistenceUnits = XMLUtils.getElementsByTagName(rootElement, "persistence-unit");
            for (Element element : persistenceUnits) {
                EntityManagerFactoryImpl emFactory = createEntityManagerFactory(element);
                emFactoryMap.put(emFactory.getUnitName(), emFactory);
                // 只有容器管理的时候，才注册到 JNDi
                if (isContainerManaged()) {
                    //注入的应该是：jta-data-source
                    JNDIContextHelper.getInitalContext().bind(((StandardXAPoolDataSource)emFactory.getDataSource()).getDataSourceName(), emFactory);
                }
            }
        }
        catch (Exception e) {
            logger.error("Create document for " + PERSISTENCE_CONFIG_FILE + " error!", e);
        }
    }


    public EntityTransaction getEntityTransaction() {
        return entityTransaction;
    }

    private EntityManagerFactoryImpl createEntityManagerFactory(Element element) throws Exception {
        String unitName = XMLUtils.getAtrributeValue(element, "name");
        String jndiName = "java:/" + unitName;
        String jtaDataSource = XMLUtils.getChildElementValueByTagName(element, "jta-data-source");
        if (jtaDataSource != null && !jtaDataSource.trim().equals("")) {
            jndiName = jtaDataSource;
        }

        Map<String, String> properties = new HashMap<String, String>();
        List<Element> propertysElements = XMLUtils.getElementsByTagName(element, "property");
        for (Element propElement : propertysElements) {
            properties.put(XMLUtils.getAtrributeValue(propElement, "name"), XMLUtils.getAtrributeValue(propElement, "value"));
        }

        StandardXADataSource sxds = new StandardXADataSource();
        StandardXAPoolDataSource sxpds = new StandardXAPoolDataSource();
        sxpds.setJdbcTestStmt("select 1");
        // check connection after checkOut from xapool, if closed, expire it and reconnect
        sxpds.setCheckLevelObject(4);
        sxpds.setDataSourceName(jndiName);

        // cache config
        CacheConfig cacheConfig = null;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (name.equalsIgnoreCase("driver")) {
                sxds.setDriverName(value);
            }
            else if (name.equalsIgnoreCase("url")) {
                sxds.setUrl(value);
            }
            else if (name.equalsIgnoreCase("username")) {
                sxpds.setUser(value);
                sxds.setUser(value);
            }
            else if (name.equalsIgnoreCase("password")) {
                sxds.setPassword(value);
                sxpds.setPassword(value);
            }
            else if (name.equals("checkLevelObject")) {
                sxpds.setCheckLevelObject(Integer.parseInt(value));
            }
            else if (name.equalsIgnoreCase("minSize")) {
                sxpds.setMinSize(Integer.parseInt(value));
            }
            else if (name.equalsIgnoreCase("maxSize")) {
                sxpds.setMaxSize(Integer.parseInt(value));
            }
            else if (name.equalsIgnoreCase("lifeTime")) {
                sxpds.setLifeTime(Long.parseLong(value));
            }
            else if (name.equalsIgnoreCase("sleepTime")) {
                sxpds.setSleepTime(Long.parseLong(value));
            }
            else if (name.equalsIgnoreCase("deadLockRetryWait")) {
                sxpds.setDeadLockRetryWait(Long.parseLong(value));
            }
            else if (name.equalsIgnoreCase("deadLockMaxWait")) {
                sxpds.setDeadLockMaxWait(Long.parseLong(value));
            }
            else if (name.startsWith(QUERY_HINT_KEY_FORM_CAHCE_PREFIX)) {
                // construct cache config
                if (cacheConfig == null) {
                    cacheConfig = new CacheConfig(unitName);
                }
                String property = name.substring(name.lastIndexOf(".") + 1);
                if (property.equalsIgnoreCase("TTL")) {
                    cacheConfig.setTTL(Long.parseLong(value));
                }
                else if (property.equalsIgnoreCase("algorithm")) {
                    if (value.equalsIgnoreCase("LFU")) {
                        cacheConfig.setAlgorithm(CacheConfig.Algorithm.LFU);
                    }
                    else if (value.equalsIgnoreCase("FIFO")) {
                        cacheConfig.setAlgorithm(CacheConfig.Algorithm.FIFO);
                    }
                    else {
                        cacheConfig.setAlgorithm(CacheConfig.Algorithm.LRU);
                    }
                }
                else if (property.equalsIgnoreCase("maxIdleTime")) {
                    cacheConfig.setMaxIdleTime(Long.parseLong(value));
                }
                else if (property.equalsIgnoreCase("maxSize")) {
                    cacheConfig.setMaxSize(Integer.parseInt(value));
                }
                else if (property.equalsIgnoreCase("maxMemorySize")) {
                    cacheConfig.setMaxMemorySize(Long.parseLong(value));
                }
                else {
                    logger.warn("Illegal JPA cache property name: " + name);
                }
            }
            else {
                logger.warn("Illegal JPA persistence.xml property name: " + name);
            }
        }
        sxpds.setTransactionManager(transactionManager);
        sxpds.setDataSource(sxds);

        // create EntityManagerFactory
        return new EntityManagerFactoryImpl(unitName, jndiName, this, sxpds, cacheConfig);
    }

    private void registerNamedQueriesByClasses(Class[] classes) {
        for (Class<?> beanClass : classes) {
            List<String> queryNames = new ArrayList<String>();
            if (beanClass.isAnnotationPresent(NamedNativeQueries.class)) {
                NamedNativeQueries namedNativeQueries = beanClass.getAnnotation(NamedNativeQueries.class);
                for (NamedNativeQuery namedNativeQuery : namedNativeQueries.value()) {
                    this.registerNamedQuery(namedNativeQuery, beanClass);
                    queryNames.add(namedNativeQuery.name());
                }
            }
            if (beanClass.isAnnotationPresent(NamedNativeQuery.class)) {
                NamedNativeQuery namedNativeQuery = beanClass.getAnnotation(NamedNativeQuery.class);
                this.registerNamedQuery(namedNativeQuery, beanClass);
                queryNames.add(namedNativeQuery.name());
            }
            logger.info("Register NamedQuery for Class: " + beanClass.getName() + ", " + Arrays.toString(queryNames.toArray(new String[queryNames.size()])));
        }
    }

    public void registerNamedQuery(NamedNativeQuery namedNativeQuery, Class<?> definedClass) {
        if (namedSQLTemplates.containsKey(namedNativeQuery.name())) {
            logger.warn("NamedQuery " + namedNativeQuery.name() + " has registered by " + namedSQLTemplates.get(namedNativeQuery.name()).getDefinedClass() + ".");
        }
        else {
            NamedSQLTemplate sqlTemplate = new NamedSQLTemplate(namedNativeQuery, definedClass);
            QueryHint[] hints = namedNativeQuery.hints();
            if (hints.length > 0) {
                // 检查 jdbc.compatible
                for (QueryHint hint : hints) {
                    if (hint.name().equals(QUERY_HINT_KEY_FOR_JDBC_COMPATIBLE)) {
                        String compatibleDatabaseTypes = hint.value();
                        for(String compatibleDbType : compatibleDatabaseTypes.split(",")){
                            //query_name = getUserInfo.MYSQL
                            compatibleDbType = compatibleDbType.trim();
                            if(compatibleDbType.length() > 0) {
                                namedSQLTemplates.put(sqlTemplate.getName() + "." + compatibleDbType.trim().toUpperCase(), sqlTemplate);
                            }
                        }
                    }
                    else {
                        namedSQLTemplates.put(sqlTemplate.getName(), sqlTemplate);
                    }
                }
            }
            else {
                namedSQLTemplates.put(sqlTemplate.getName(), sqlTemplate);
            }
        }
    }

    public NamedSQLTemplate getNamedQuery(String name, String dbType) {
        NamedSQLTemplate sqlTemplate = namedSQLTemplates.get(name + "." + dbType.toUpperCase());
        if(sqlTemplate == null) {
            sqlTemplate = namedSQLTemplates.get(name);
        }
        return sqlTemplate;
    }

    public Document getPersistenceXMLDocument() {
        return xmlDocument;
    }

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryBuilderImpl.getEntityManagerFactoryByName("DefaultMysqlDS");
        System.out.println(entityManagerFactory);
    }
}
