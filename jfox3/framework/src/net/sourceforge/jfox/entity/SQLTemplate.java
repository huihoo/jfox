package net.sourceforge.jfox.entity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.PersistenceException;

import net.sourceforge.jfox.entity.annotation.MappedColumn;
import net.sourceforge.jfox.entity.annotation.ParameterMap;
import net.sourceforge.jfox.entity.dao.MapperEntity;
import net.sourceforge.jfox.util.AnnotationUtils;
import net.sourceforge.jfox.util.ClassUtils;

/**
 * 通过 createQuery 调用创建的 SQLTemplate，没有名称
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */

public class SQLTemplate {

    protected String templateSQL;

    protected Class<?> resultClass;

    /**
     * 保存一个 Result Class 的 column name=>column entry 的对应关系
     *  result class => {column name=> column entry}
     */
    protected static Map<Class<?>, Map<String, ColumnEntry>> resultClass2MappedColumnMap = new HashMap<Class<?>, Map<String, ColumnEntry>>();

//    @SuppressWarnings({"unchecked"})
    public SQLTemplate(String sqlTemplate, Class<?> resultClass) {
        this.templateSQL = sqlTemplate;
        this.resultClass = resultClass;
        if(resultClass.equals(void.class)) {
            // @NamedNativeQuery default value is void.class, we want to use EntityObject.class as default
            this.resultClass = EntityObject.class;
        }

        // 存储 ResultClass 的 column 及 类型
        introspectResultClass(getResultClass());
    }

    protected void introspectResultClass(Class<?> resultClass) {
        if(ClassUtils.isPrimitiveClass(resultClass)) {
            return;
        }
        else if(MapperEntity.class.isAssignableFrom(resultClass)) {
            return ;
        }
        else if(String.class.equals(resultClass) || ClassUtils.isPrimitiveClass(resultClass) || ClassUtils.isPrimitiveWrapperClass(resultClass)) {
            return;
        }
        
        if(!resultClass.isInterface()){
            throw new PersistenceException("Not supported result class: " + resultClass.getName() + ", only support primitive class, EntityObject, and @Entity interface.");
        }

        if(resultClass2MappedColumnMap.containsKey(resultClass)) {
            return ;
        }
        Map<String, ColumnEntry> columnMap = new HashMap<String, ColumnEntry>();
        Method[] columnMethods = AnnotationUtils.getAnnotatedMethods(resultClass, Column.class);
        for(Method columnMethod : columnMethods){
            Column column = columnMethod.getAnnotation(Column.class);
            ColumnEntry columnEntry = new ColumnEntry();
            columnEntry.name = column.name();
            columnEntry.type = columnMethod.getReturnType();
            columnMap.put(column.name(), columnEntry);
        }

        Method[] mappedColumnMethods = AnnotationUtils.getAnnotatedMethods(resultClass, MappedColumn.class);
        for(Method mappedColumnMethod : mappedColumnMethods){
            MappedColumn mappedColumn = mappedColumnMethod.getAnnotation(MappedColumn.class);
            MappedColumnEntry mcEntry = new MappedColumnEntry();
            // 用方法名作为 name, 也即为 resultMap 中的 key
            mcEntry.name = MapperEntity.getColumnMapKeyByMethod(mappedColumnMethod);
            mcEntry.namedQuery = mappedColumn.namedQuery();
            mcEntry.type = mappedColumnMethod.getReturnType();
            mcEntry.params = mappedColumn.params();
            columnMap.put(mcEntry.name,mcEntry);
        }

        resultClass2MappedColumnMap.put(resultClass,columnMap);
    }

    public Class<?> getColumnClass(String columnName){
        ColumnEntry columnEntry = resultClass2MappedColumnMap.get(getResultClass()).get(columnName);
        if(columnEntry == null) {
            return null;
        }
        else {
            return columnEntry.type;
        }
    }

    /**
     * 得到 Result 的 Columns
     */
    public Set<String> getColumns(){
        return resultClass2MappedColumnMap.get(getResultClass()).keySet();
    }

    /**
     * 得到所有的 ColumnEntry
     */
    public Collection<MappedColumnEntry> getMappedColumnEntries(){
        List<MappedColumnEntry> mappedColumnEntries = new ArrayList<MappedColumnEntry>();
        for(ColumnEntry entry : resultClass2MappedColumnMap.get(getResultClass()).values()){
            if(entry instanceof MappedColumnEntry) {
                mappedColumnEntries.add((MappedColumnEntry)entry);
            }
        }
        return mappedColumnEntries;
    }


    public String getTemplateSQL() {
        return templateSQL;
    }

    public Class<?> getResultClass(){
        return this.resultClass;
    }

    // @Column
    public static class ColumnEntry {
        String name;
        Class type;
    }

    // MappedColumn
    public static class MappedColumnEntry extends ColumnEntry {
        String namedQuery;
        ParameterMap[] params;
    }
}
