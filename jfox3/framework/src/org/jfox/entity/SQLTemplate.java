package org.jfox.entity;

import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * 通过 createQuery 调用创建的 SQLTemplate，没有名称
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public class SQLTemplate {

    protected String templateSQL;

    protected Class<?> resultClass;

    /**
     * 保存一个 Result Class 的 column name=>column entry 的对应关系
     *  result class => {column name=> column entry}
     */
//    protected static Map<Class<?>, Map<String, ColumnEntry>> resultClass2MappedColumnMap = new HashMap<Class<?>, Map<String, ColumnEntry>>();

    protected Logger logger = Logger.getLogger(this.getClass());

//    @SuppressWarnings({"unchecked"})
    public SQLTemplate(String sqlTemplate, Class<?> resultClass) {
        this.templateSQL = sqlTemplate;
        this.resultClass = resultClass;
        if(resultClass.equals(void.class)) {
            // @NamedNativeQuery default value is void.class, we want to use EntityObject.class as default
            this.resultClass = MappedEntity.class;
        }

        // 存储 ResultClass 的 column 及 类型
        introspectResultClass(getResultClass());
    }

    protected void introspectResultClass(Class<?> resultClass) {
        EntityFactory.introspectResultClass(resultClass);
    }

    public Class<?> getColumnClass(String columnName){
        EntityFactory.ColumnEntry columnEntry = EntityFactory.getColumnEntry(getResultClass(), columnName);
        if(columnEntry == null) {
            return null;
        }
        else {
            return columnEntry.field.getType();
        }
    }

    /**
     * 得到所有的 ColumnEntry
     */
    public Collection<EntityFactory.MappedColumnEntry> getMappedColumnEntries(){
        return EntityFactory.getMappedColumnEntries(getResultClass());
    }


    public String getTemplateSQL() {
        return templateSQL;
    }

    public Class<?> getResultClass(){
        return this.resultClass;
    }

}
