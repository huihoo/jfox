package net.sourceforge.jfox.entity;

import javax.persistence.NamedNativeQuery;

/**
 * 用来保存 NamedQuery
 * 然后根据参数，使用 velocity 构造 SQL
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class NamedSQLTemplate extends SQLTemplate {

    /**
     * 定义 NamedQuery 的 DAO class
     */
    private Class<?> definedClass = null;

    private NamedNativeQuery namedNativeQuery = null;

    public NamedSQLTemplate(NamedNativeQuery namedNativeQuery, Class<?> definedClass) {
        super(namedNativeQuery.query(), namedNativeQuery.resultClass());
        this.namedNativeQuery = namedNativeQuery;
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


    public static void main(String[] args) {

    }
}
