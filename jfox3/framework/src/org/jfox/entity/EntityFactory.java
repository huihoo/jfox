package org.jfox.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.PersistenceException;

import org.jfox.entity.annotation.MappedColumn;
import org.jfox.entity.annotation.ParameterMap;
import org.jfox.entity.MappedEntity;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.ClassUtils;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EntityFactory {
    /**
     * 保存一个 Result Class 的 column name=>column entry 的对应关系
     * result class => {column name=> column entry}
     */
    protected static Map<Class<?>, Map<String, ColumnEntry>> resultClass2MappedColumnMap = new HashMap<Class<?>, Map<String, ColumnEntry>>();

    static void introspectResultClass(Class<?> resultClass) {
        if (ClassUtils.isPrimitiveClass(resultClass)) {
            return;
        }
        else if (MappedEntity.class.isAssignableFrom(resultClass)) {
            return;
        }
        else if (String.class.equals(resultClass) || ClassUtils.isPrimitiveClass(resultClass) || ClassUtils.isPrimitiveWrapperClass(resultClass)) {
            return;
        }

        if (resultClass.isInterface()) {
            throw new PersistenceException("Not supported result class: " + resultClass.getName() + ", only support primitive class, EntityObject, and @Entity Class.");
        }

        if (resultClass2MappedColumnMap.containsKey(resultClass)) {
            return;
        }
        Map<String, ColumnEntry> columnMap = new HashMap<String, ColumnEntry>();
        Field[] columnMethods = AnnotationUtils.getAnnotatedFields(resultClass, Column.class);
        for (Field columnMethod : columnMethods) {
            columnMethod.setAccessible(true);
            Column column = columnMethod.getAnnotation(Column.class);
            ColumnEntry columnEntry = new ColumnEntry();
            String columnName = column.name();
            if (columnName.equals("")) {
                columnName = columnMethod.getName().toUpperCase();
            }
            columnEntry.name = columnName;
            columnEntry.field = columnMethod;
            columnMap.put(column.name(), columnEntry);
        }

        Field[] mappedColumnMethods = AnnotationUtils.getAnnotatedFields(resultClass, MappedColumn.class);
        for (Field mappedColumnMethod : mappedColumnMethods) {
            MappedColumn mappedColumn = mappedColumnMethod.getAnnotation(MappedColumn.class);
            MappedColumnEntry mcEntry = new MappedColumnEntry();
            mcEntry.name = mappedColumnMethod.getName().toUpperCase();
            mcEntry.namedQuery = mappedColumn.namedQuery();
            mcEntry.field = mappedColumnMethod;
            mcEntry.params = mappedColumn.params();
            columnMap.put(mcEntry.name, mcEntry);
        }

        resultClass2MappedColumnMap.put(resultClass, columnMap);
    }

    public static Object newEntityObject(Class<?> resultClass, Map<String, Object> resultMap) {
        if (resultClass.equals(EntityObject.class)) {
            return MappedEntity.newEntityObject(resultClass, resultMap);
        }
        else {
            // 从 SQLTemplate 中获得 resultClass 的对应信息，然后Field.set
            try {
                Object entity = resultClass.newInstance();
                for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                    String columnName = entry.getKey();
                    ColumnEntry columnEntry = getColumnEntry(resultClass, columnName);
                    if (columnEntry != null) {
                        columnEntry.field.set(entity, entry.getValue());
                    }
                }
                return entity;
            }
            catch (Exception e) {
                throw new PersistenceException("Create Entity for Class: " + resultClass.getName() + " failed.", e);
            }
        }
    }

    public static void appendMappedColumn(Object entity, Map<String, Object> mappedColumnResultMap) {
        // 添加MapColumn到EntityObject
        if (entity.getClass().equals(MappedEntity.class)) {
            for (Map.Entry<String, Object> entry : mappedColumnResultMap.entrySet()) {
                ((MappedEntity)entity).setColumnValue(entry.getKey(), entry.getValue());
            }
        }
        else {
            try {
                for (Map.Entry<String, Object> entry : mappedColumnResultMap.entrySet()) {
                    String columnName = entry.getKey();
                    ColumnEntry columnEntry = getColumnEntry(entry.getClass(), columnName);
                    if (columnEntry != null) {
                        columnEntry.field.set(entity, entry.getValue());
                    }
                }
            }
            catch (Exception e) {
                throw new PersistenceException("Append Entity MappedColumn for Class: " + entity.getClass().getName() + " failed.", e);
            }
        }
    }


    public static Collection<MappedColumnEntry> getMappedColumnEntries(Class<?> resultClass) {
        List<MappedColumnEntry> mappedColumnEntries = new ArrayList<MappedColumnEntry>();
        Map<String, ColumnEntry> entries = resultClass2MappedColumnMap.get(resultClass);
        if (entries != null && !entries.isEmpty()) {
            for (EntityFactory.ColumnEntry entry : resultClass2MappedColumnMap.get(resultClass).values()) {
                if (entry instanceof EntityFactory.MappedColumnEntry) {
                    mappedColumnEntries.add((EntityFactory.MappedColumnEntry)entry);
                }
            }
        }
        return mappedColumnEntries;
    }

    public static ColumnEntry getColumnEntry(Class<?> resultClass, String columnName) {
        return resultClass2MappedColumnMap.get(resultClass).get(columnName);
    }

    // @Column
    public static class ColumnEntry {
        String name;
        Field field;
    }

    // @MappedColumn
    public static class MappedColumnEntry extends ColumnEntry {
        String namedQuery;
        ParameterMap[] params;
    }
}
