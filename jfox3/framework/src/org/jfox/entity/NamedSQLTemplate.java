package org.jfox.entity;

import javax.persistence.NamedNativeQuery;
import javax.persistence.QueryHint;

/**
 * 用来保存 NamedQuery
 * 然后根据参数，使用 velocity 构造 SQL
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class NamedSQLTemplate extends SQLTemplate {

    /**
     * 定义 NamedQuery 的 DAO class
     */
    private Class<?> definedClass = null;

    private NamedNativeQuery namedNativeQuery = null;

    /**
     * 使用的 cache config
     * cache config 在 persistence.xml 定义
     */
    protected String cacheConfigName = "";

    /**
     * 使用的 cache partition，如果没有，则不使用缓存
     * cache partition 在 NamedNativeQuery 中定义
     */
    protected String cachePartition = "";

    public static final String CACHE_CONFIG_NAME = "cache.config";
    public static final String CACHE_PARTITION_NAME = "cache.partition";

    public NamedSQLTemplate(NamedNativeQuery namedNativeQuery, Class<?> definedClass) {
        super(namedNativeQuery.query(), namedNativeQuery.resultClass());
        this.namedNativeQuery = namedNativeQuery;
        // 解析 QueryHint，比如 (name="cache.config", value="product")
        for(QueryHint hint : namedNativeQuery.hints()){
            String name = hint.name();
            if(name.equals(CACHE_CONFIG_NAME)) {
                cacheConfigName = hint.value();
            }
            if(name.equals(CACHE_PARTITION_NAME)) {
                cachePartition = hint.value();
            }
        }

        this.definedClass = definedClass;
    }

    public Class<?> getDefinedClass() {
        return definedClass;
    }

    /**
     * the name of NamedQuery
     */
    public String getName() {
        return namedNativeQuery.name();
    }

    public String getCacheConfigName() {
        return cacheConfigName;
    }

    public String getCachePartition() {
        return cachePartition;
    }

    public static void main(String[] args) {

    }
}
