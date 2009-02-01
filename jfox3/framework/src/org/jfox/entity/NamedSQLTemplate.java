/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

/**
 * 用来保存 NamedQuery
 * 然后根据参数，使用 velocity 构造 SQL
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class NamedSQLTemplate extends SQLTemplate {

    public static final String DEFAULT_CACHE_PARTITION = "__DEFAULT__";

    /**
     * 定义 NamedQuery 的 DAO class
     */
    private Class<?> definedClass = null;

//    private NamedNativeQuery namedNativeQuery = null;

    /**
     * 使用的 cache partition，如果没有，则不使用缓存
     * cache partition 在 NamedNativeQuery 中定义
     */
    protected String cachePartition = "";

    protected String name = "";

    public static final String CACHE_PARTITION_NAME = "cache.partition";

/*
    public NamedSQLTemplate(NamedNativeQuery namedNativeQuery, Class<?> definedClass) {
        super(namedNativeQuery.query(), namedNativeQuery.resultClass());
        this.namedNativeQuery = namedNativeQuery;
        // 解析 QueryHint，比如 (name="cache.config", value="product")
        for(QueryHint hint : namedNativeQuery.hints()){
            String name = hint.name();
            if(name.equals(CACHE_PARTITION_NAME)) {
                cachePartition = hint.value();
                logger.info("Use cache partition: " + cachePartition + " for named query: " + getName());
            }
        }

        this.definedClass = definedClass;
    }
*/

    public NamedSQLTemplate(String name, String sqlTemplate, Class resultClass, Class<?> defineClass, String partition) {
        super(sqlTemplate, resultClass);
        if(partition == null) {
            this.cachePartition = DEFAULT_CACHE_PARTITION; 
        }
        else {
            this.cachePartition = partition;
        }
        if(name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("name is null");
        }
        this.name = name;
        this.definedClass = defineClass;
    }

    public Class<?> getDefinedClass() {
        return definedClass;
    }

    /**
     * the name of NamedQuery
     */
    public String getName() {
        return name;
    }

    public String getCachePartition() {
        return cachePartition;
    }

    public static void main(String[] args) {

    }
}
