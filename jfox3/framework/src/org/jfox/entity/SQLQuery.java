/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.jfox.entity.annotation.ParameterMap;
import org.jfox.entity.cache.Cache;
import org.jfox.entity.cache.CacheConfig;
import org.jfox.util.ClassUtils;
import org.jfox.util.VelocityUtils;

/**
 * 负责根据根据 SQLTemplate 构造 PreparedStatement，并执行，返回 ResultClass
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class SQLQuery extends QueryExt {

    protected static Logger logger = Logger.getLogger(SQLQuery.class);

    private EntityManagerImpl em;
    private SQLTemplate sqlTemplate = null;

    //保存使用 setParameter 设置参数，用于 VelocityContext 的数据
    private Map<String, Object> parameterMap = new HashMap<String, Object>();

    private String nativeSQL;

    private boolean isNamedQuery = false;

    private QueryCacheKey cacheKey = null;

    public SQLQuery(EntityManagerImpl em, SQLTemplate sqlTemplate) {
        this.em = em;
        this.sqlTemplate = sqlTemplate;
        if (sqlTemplate instanceof NamedSQLTemplate) {
            isNamedQuery = true;
        }
    }

    public boolean isNamedQuery() {
        return isNamedQuery;
    }

    public String getName() {
        if (sqlTemplate instanceof NamedSQLTemplate) {
            return ((NamedSQLTemplate)sqlTemplate).getName();
        }
        else {
            return "";
        }
    }

    private SQLTemplate getSQLTemplate() {
        return sqlTemplate;
    }

    public Query setParameter(String name, Object value) {
        parameterMap.put(name, value);
        return this;
    }

    private synchronized QueryCacheKey getCacheKey() {
        if (cacheKey == null) {
            cacheKey = new QueryCacheKey(getName(), parameterMap, getFirstResult(), getMaxResult());
        }
        return cacheKey;
    }

    public int executeUpdate() {
        try {
            PreparedStatement pst = buildPreparedStatement();
            int rows = pst.executeUpdate();
            // close PreparedStatement
            pst.close();
            tryFlushCache(); // update successfully, clear cache
            return rows;
        }
        catch (SQLException e) {
            throw new PersistenceException("SQLQuery.executeUpdate exception.", e);
        }

    }

    public List<?> getResultList() {

        Object cachedResult = tryRetrieveCache();
        if (cachedResult != null) {
            return (List<?>)cachedResult;
        }

        PreparedStatement pst = null;
        ResultSet rset = null;
        final List<Object> results = new ArrayList<Object>();
        try {
            pst = buildPreparedStatement();
            rset = pst.executeQuery();
            // Skip Results
            if (getFirstResult() > 0) {
                try {
                    rset.absolute(getFirstResult() + 1); // absolute start from 1, but firstResult start 0
                }
                catch(SQLException e) { // not support
                    for (int i = 0; i < getFirstResult(); i++) {
                        if (!rset.next()) {
                            return results;
                        }
                    }
                }
            }

            int countResult = 0;

            while (rset.next() && (countResult++ < getMaxResult())) {
                results.add(buildResultObject(rset));
            }

            tryStoreCache(results);

            return results;
        }
        catch (SQLException e) {
            throw new PersistenceException("SQLQuery.getResultList exception.", e);
        }
        finally {
            //close PreparedStatement
            try {
                if (rset != null) {
                    rset.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pst != null) {
                    pst.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getSingleResult() {
        Object cachedObject = tryRetrieveCache();
        if (cachedObject != null) {
            return cachedObject;
        }

        PreparedStatement pst = null;
        ResultSet rset = null;
        try {
            pst = buildPreparedStatement();
            rset = pst.executeQuery();

            if (rset.next()) {
                Object result = buildResultObject(rset);
                tryStoreCache(result);
                return result;
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            throw new PersistenceException("SQLQuery.getSingleResult exception.", e);
        }
        finally {
            //close PreparedStatement
            try {
                if (rset != null) {
                    rset.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pst != null) {
                    pst.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用 velocity 构造 PreparedStatement，将 args 设置到 PreparedStatement 的 Parameter 中
     * Parameter 使用 p1 p2 p3 编号
     *
     * @throws SQLException sql exception
     */
    protected PreparedStatement buildPreparedStatement() throws SQLException {
        // velocity expressions
        final List<String> expressions = new ArrayList<String>();
        // expression's result
        final List<Object> expressionResults = new ArrayList<Object>();

        // nativeSQL 不一定是固定的，比如有 if 判断的时候，所以每次都要生成
        nativeSQL = VelocityUtils.evaluate(sqlTemplate.getTemplateSQL(), parameterMap, new ReferenceInsertionEventHandler() {
            public Object referenceInsert(String reference, Object value) {
                expressions.add(reference);
                expressionResults.add(value);
                return "?";
            }
        });

        logger.info("Building PreparedStatemenet use SQL: " + nativeSQL);

        Connection connection = em.getConnection();
        PreparedStatement pst = connection.prepareStatement(nativeSQL);

        for (int i = 0; i < expressions.size(); i++) {

            // 不能都是用 setString/setobject, 需要使用正确类型,但是该类型无法判断
            Object parameterResult = expressionResults.get(i);
            if (parameterResult == null) {
                String msg = "Get null while evalute " + expressions.get(i);
                if(isNamedQuery()) {
                    msg += " in named qeury: " + ((NamedSQLTemplate)sqlTemplate).getName();
                }
                logger.warn(msg);
            }
            setPreparedStatementParameter(pst, i + 1, expressionResults.get(i));
        }

        return pst;

    }

    /**
     * 因为给出 result 的类型，调用正确的 PreparedStatement.setXXX 方法
     *
     * @param pst   pst
     * @param index index
     * @param value value
     * @throws SQLException sql exception
     */
    protected void setPreparedStatementParameter(PreparedStatement pst, int index, Object value) throws SQLException {
        if(value == null) {
            pst.setObject(index, null);
            return;
        }
        Class<?> clazz = value.getClass();

        if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            pst.setBoolean(index, (Boolean)value);
        }
        else if (Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            pst.setByte(index, (Byte)value);
        }
        else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            pst.setShort(index, (Short)value);
        }
        else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            pst.setInt(index, (Integer)value);
        }
        else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            pst.setLong(index, (Long)value);
        }
        else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            pst.setFloat(index, (Float)value);
        }
        else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            pst.setDouble(index, (Double)value);
        }
        else if (java.math.BigDecimal.class.equals(clazz)) {
            pst.setBigDecimal(index, ((java.math.BigDecimal)value));
        }
        else if (String.class.equals(clazz)) {
            pst.setString(index, ((String)value));
        }
        else if (Date.class.equals(clazz)) {
            pst.setDate(index, new java.sql.Date(((Date)value).getTime()));
        }
        else if (java.sql.Date.class.equals(clazz)) {
            pst.setDate(index, (java.sql.Date)value);
        }
        else if (Timestamp.class.equals(clazz)) {
            pst.setTimestamp(index, (Timestamp)value);
        }
        else {
            pst.setObject(index, value);
        }
    }

    protected Object buildResultObject(ResultSet rset) throws SQLException {
        //需要判断 ResultClass 类型
        Class<?> resultClass = sqlTemplate.getResultClass();

        if ((!resultClass.isInterface() && resultClass.isAnnotationPresent(Entity.class)) || MappedEntity.class.isAssignableFrom(resultClass)) {
            return buildEntityObject(rset);
        }
        else if (resultClass.equals(String.class) || ClassUtils.isPrimitiveClass(resultClass) || ClassUtils.isPrimitiveWrapperClass(resultClass)) {
            return getCorrectResult(rset, resultClass, 1);
        }
        else {
            throw new PersistenceException("Not supported result class: " + resultClass);
        }
    }

    /**
     * 将 ResultSet 构造成 EntityObject
     *
     * @param rset result set
     * @throws SQLException if failed
     */
    protected Object buildEntityObject(ResultSet rset) throws SQLException {
        final Map<String, Object> resultMap = new HashMap<String, Object>();
        Class<?> resultClass = sqlTemplate.getResultClass();
        ResultSetMetaData rsetMeta = rset.getMetaData();

        //注意： 有可能只查部分字段，不能使用 sqlTemplate.getColumnsByResultClass()
        for (int i = 1; i <= rsetMeta.getColumnCount(); i++) {
            String columnName = rsetMeta.getColumnName(i);

            if (resultClass.equals(MappedEntity.class)) {
                // 是 MappedEntity，无法获得Column Class信息，统一取 String
                resultMap.put(columnName, rset.getString(columnName));
            }
            else {
                Class columnClass = sqlTemplate.getColumnClass(columnName);
                // 如果 columnClass == null，说明 Entity中没有该 @Column，那么无需设置到 resultMap
                if (columnClass == null) {
                    logger.warn("No column named \"" + columnName + "\" in result class " + resultClass.getName() + " when execute sql query: " + nativeSQL);
                }
                else {
                    //设置为 DataObject @Column 的类型
                    resultMap.put(columnName, getCorrectResult(rset, columnClass, i));
                }

            }
        }

        Object dataObject = EntityFactory.newEntityObject(resultClass, resultMap);

        // deal with MappedColumn
        final Map<String, Object> mappedColumnResultMap = new HashMap<String, Object>();
        boolean isMappedColumnSet = false;
        for (EntityFactory.MappedColumnEntry mappedColEntry : sqlTemplate.getMappedColumnEntries()) {
            ParameterMap[] params = mappedColEntry.params;
            final List<Object> parameterResult = new ArrayList<Object>();

            final Map<String, Object> velocityMap = new HashMap<String, Object>();
            // $this 表示本 data object
            velocityMap.put("this", dataObject);

            // 测试参数是否有值，只有在有值的情况才设置 MappedColumn
            final Map<String, Boolean> mappedColumnSetFlag = new HashMap<String, Boolean>(1);
            final String EVALUATE_KEY = "evaluated";
            mappedColumnSetFlag.put(EVALUATE_KEY, true);
            EventHandler eventHandler = new ReferenceInsertionEventHandler() {
                public Object referenceInsert(String reference, Object value) {
                    if (value == null || value.equals("") || value.equals(reference)) {
                        mappedColumnSetFlag.put(EVALUATE_KEY, false);
                    }
                    parameterResult.add(value);
                    return value;
                }
            };

            for (ParameterMap parameterMap : params) {
                // 有可能抛出异常
                VelocityUtils.evaluate(parameterMap.value(), velocityMap, eventHandler);
            }

            // MappedColumn 需要的参数都已经赋值，没有赋值的话说明该次查询也不需要 MappedColumn 的值
            if (mappedColumnSetFlag.get(EVALUATE_KEY)) {
                isMappedColumnSet = true;
                QueryExt mappedColumnQuery = em.createNamedQuery(mappedColEntry.namedQuery);
                for (int i = 0; i < params.length; i++) {
                    mappedColumnQuery.setParameter(params[i].name(), parameterResult.get(i));
                }

                if (mappedColEntry.field.getType().isArray()) { // array
                    mappedColumnResultMap.put(mappedColEntry.name, mappedColumnQuery.getResultList().toArray());
                }
                else if (Collection.class.isAssignableFrom(mappedColEntry.field.getType())) { // Collection
                    mappedColumnResultMap.put(mappedColEntry.name, mappedColumnQuery.getResultList());
                }
                else { // single
                    mappedColumnResultMap.put(mappedColEntry.name, mappedColumnQuery.getSingleResult());
                }
            }
        }
        if (isMappedColumnSet) {
            EntityFactory.appendMappedColumn(dataObject, mappedColumnResultMap);
        }
        return dataObject;
    }

    protected Object getCorrectResult(ResultSet rset, Class columnClass, int columnIndex) throws SQLException {
        Object value = null;
        if (Boolean.class == columnClass || boolean.class == columnClass) {
            value = rset.getBoolean(columnIndex);
        }
        else if (Byte.class == columnClass || byte.class == columnClass) {
            value = rset.getByte(columnIndex);
        }
        else if (Short.class == columnClass || short.class == columnClass) {
            value = rset.getShort(columnIndex);
        }
        else if (Integer.class == columnClass || int.class == columnClass) {
            value = rset.getInt(columnIndex);
        }
        else if (Long.class == columnClass || long.class == columnClass) {
            value = rset.getLong(columnIndex);
        }
        else if (Float.class == columnClass || float.class == columnClass) {
            value = rset.getFloat(columnIndex);
        }
        else if (Double.class == columnClass || double.class == columnClass) {
            value = rset.getDouble(columnIndex);
        }
        else if (BigDecimal.class == columnClass) {
            value = rset.getBigDecimal(columnIndex);
        }
        else if (byte[].class == columnClass) {
            try {
                InputStream in = rset.getBinaryStream(columnIndex);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int n;
                byte[] buffer = new byte[1024];
                while ((n = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, n);
                }
                value = baos.toByteArray();
                in.close();
            }
            catch (IOException e) {
                value = rset.getBytes(columnIndex);
            }
        }
        else if (java.lang.String.class == columnClass) {
            //deal with CLOB, 使用 rset.getCharactorStream 统一处理
/*
            try {
                Reader reader = rset.getCharacterStream(columnIndex);
                StringWriter sw = new StringWriter();

                int n;
                char[] buffer = new char[1024];
                while (-1 != (n = reader.read(buffer))) {
                    sw.write(buffer, 0, n);
                }
                value = sw.toString();
            }
            catch(IOException e) {
                value = rset.getString(columnIndex);
            }
*/

            if (rset.getMetaData().getColumnType(columnIndex) == Types.CLOB) {
                Clob clob = rset.getClob(columnIndex);
                if (clob != null) {
                    value = clob.getSubString(1, (int)clob.length());
                }
            }
            else {
                value = rset.getString(columnIndex);
            }

        }
        else if (Date.class == columnClass) {
            java.sql.Date sqldate = rset.getDate(columnIndex);
            if (sqldate != null) {
                value = new Date(sqldate.getTime());
            }
        }
        else if (java.sql.Date.class == columnClass) {
            value = rset.getDate(columnIndex);
        }
        else if (Timestamp.class == columnClass) {
            value = rset.getTimestamp(columnIndex);
        }
        else if (java.lang.Object.class == columnClass) {
            value = rset.getObject(columnIndex);
        }
        return value;
    }

    Object tryRetrieveCache() {
        if (isNamedQuery()) {
            String cachePartition = ((NamedSQLTemplate)getSQLTemplate()).getCachePartition();
            CacheConfig cacheConfig = EntityManagerFactoryBuilderImpl.getCacheConfig(em.getUnitName());
            if (cacheConfig != null) {
                Cache cache = cacheConfig.buildCache(cachePartition);
                QueryCacheKey key = getCacheKey();
                return cache.get(key);
            }
        }
        return null;
    }

    void tryStoreCache(Object result) {
        if (result == null) {
            return;
        }
        if (!(result instanceof Serializable)) {
            logger.warn("Store cache failed, result is not Serializable! " + result);
            return;
        }
        if (isNamedQuery()) {
            String cachePartition = ((NamedSQLTemplate)getSQLTemplate()).getCachePartition();
            CacheConfig cacheConfig = EntityManagerFactoryBuilderImpl.getCacheConfig(em.getUnitName());
            if (cacheConfig != null) {
                Cache cache = cacheConfig.buildCache(cachePartition);
                QueryCacheKey key = getCacheKey();
                cache.put(key, (Serializable)result);
            }
        }
    }

    void tryFlushCache() {
        if (isNamedQuery()) {
            String cachePartition = ((NamedSQLTemplate)getSQLTemplate()).getCachePartition();
            CacheConfig cacheConfig = EntityManagerFactoryBuilderImpl.getCacheConfig(em.getUnitName());
            if (cacheConfig != null) {
                Cache cache = cacheConfig.buildCache(cachePartition);
                cache.clear();
            }
        }
    }
}

//实现完整的 equals and hashCode
class QueryCacheKey implements Serializable {
    private String templateName;
    private Map<String, Object> parameterMap = new HashMap<String, Object>();
    private int startPosition = 0;
    private int maxResult = Integer.MAX_VALUE;

    public QueryCacheKey(String templateName, Map<String, Object> parameterMap, int startPosition, int maxResult) {
        this.templateName = templateName;
        this.parameterMap.putAll(parameterMap);
        this.startPosition = startPosition;
        this.maxResult = maxResult;
    }

    public String toString() {
        return templateName + ", ParameterMap" + parameterMap.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryCacheKey cacheKey = (QueryCacheKey)o;
        return templateName.equals(cacheKey.templateName)
                && (startPosition == cacheKey.startPosition)
                && (maxResult == cacheKey.maxResult)
                && parameterMap.equals(cacheKey.parameterMap);
    }

    public int hashCode() {
        int result;
        result = templateName.hashCode();
        result = 31 * result + parameterMap.hashCode();
        return result;
    }
}