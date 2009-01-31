/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.mapping;

import org.jfox.entity.MappedEntity;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.ClassUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for create Entity Object according to Entity Class and Entity data
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EntityFactory {
    /**
     * 保存一个 Result Class 的 column name=>column entry 的对应关系
     * result class => {column name=> column entry}
     */
    protected static Map<Class<?>, Map<String, ColumnEntry>> resultClass2ColumnsMap = new HashMap<Class<?>, Map<String, ColumnEntry>>();

    public static void introspectResultClass(Class<?> resultClass) {
        if (resultClass2ColumnsMap.containsKey(resultClass)) {
            return;
        }

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
            throw new PersistenceException("Not supported result class: " + resultClass.getName() + ", only support primitive class, MappedEntity, and @Entity Class.");
        }
        Map<String, ColumnEntry> columnMap = new HashMap<String, ColumnEntry>();
        Field[] columnFields = AnnotationUtils.getAnnotatedFields(resultClass, Column.class);
        for (Field columnField : columnFields) {
            columnField.setAccessible(true);
            Column column = columnField.getAnnotation(Column.class);
            ColumnEntry columnEntry = new ColumnEntry();
            String columnName = column.name();
            if (columnName.equals("")) {
                columnName = columnField.getName().toUpperCase();
            }
            columnEntry.setName(columnName);
            columnEntry.setField(columnField);
            // is PK Column
            if(columnField.isAnnotationPresent(Id.class)) {
                columnEntry.setPK(true);
            }
            columnMap.put(column.name(), columnEntry);
        }

        Field[] mappingColumnFields = AnnotationUtils.getAnnotatedFields(resultClass, JoinColumn.class);
        for (Field mappingColumnField : mappingColumnFields) {
            JoinColumn joinColumn = mappingColumnField.getAnnotation(JoinColumn.class);
            MappingColumnEntry mcEntry = new MappingColumnEntry();
            mcEntry.setName(mappingColumnField.getName().toUpperCase());
            mcEntry.setField(mappingColumnField);
            //TODO: should be specified
            mcEntry.setColumnDefinition(joinColumn.columnDefinition());
            //TODO: should be specified
            mcEntry.setReferencedColumnName(joinColumn.referencedColumnName());

            //Mapping Column 可能 OneToMany, Field 为 List<>
            if(mappingColumnField.isAnnotationPresent(OneToOne.class) || mappingColumnField.isAnnotationPresent(ManyToOne.class)) {
                mcEntry.setTargetEntity(mcEntry.getField().getType());
            }
            else if(mappingColumnField.isAnnotationPresent(OneToMany.class)){
                mcEntry.setToMany(true);
                OneToMany oneToMany = mappingColumnField.getAnnotation(OneToMany.class);
                if(!oneToMany.targetEntity().equals(void.class) && oneToMany.targetEntity() != null){
                    mcEntry.setTargetEntity(oneToMany.targetEntity());
                }
            }
            else if(mappingColumnField.isAnnotationPresent(ManyToMany.class)){
                mcEntry.setToMany(true);
                ManyToMany manyToMany = mappingColumnField.getAnnotation(ManyToMany.class);
                if(!manyToMany.targetEntity().equals(void.class) && manyToMany.targetEntity() != null){
                    mcEntry.setTargetEntity(manyToMany.targetEntity());
                }
            }
            columnMap.put(mcEntry.getName(), mcEntry);
        }

        resultClass2ColumnsMap.put(resultClass, columnMap);
    }

    public static <T> T newEntityObject(Class<T> resultClass){
        return newEntityObject(resultClass, new HashMap<String, Object>(0));
    }

    public static <T> T newEntityObject(Class<T> resultClass, Map<String, Object> resultMap) {
        if (resultClass.equals(MappedEntity.class)) {
            return (T)MappedEntity.newMappedEntity(resultMap);
        }
        else {
            // 从 SQLTemplate 中获得 resultClass 的对应信息，然后Field.set
            try {
                T entity = resultClass.newInstance();
                for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                    String columnName = entry.getKey();
                    ColumnEntry columnEntry = getColumnEntry(resultClass, columnName);
                    if (columnEntry != null) {
                        columnEntry.getField().set(entity, entry.getValue());
                    }
                }
                return entity;
            }
            catch (Exception e) {
                throw new PersistenceException("Create Entity for Class: " + resultClass.getName() + " failed.", e);
            }
        }
    }

    /**
     * 添加MapColumn到EntityObject
     * @param entity
     * @param mappedColumnResultMap
     */
    public static void appendMappingColumn(Object entity, Map<String, Object> mappedColumnResultMap) {
        if (entity.getClass().equals(MappedEntity.class)) {
            for (Map.Entry<String, Object> entry : mappedColumnResultMap.entrySet()) {
                ((MappedEntity)entity).setColumnValue(entry.getKey(), entry.getValue());
            }
        }
        else {
            try {
                for (Map.Entry<String, Object> entry : mappedColumnResultMap.entrySet()) {
                    String columnName = entry.getKey();
                    ColumnEntry columnEntry = getColumnEntry(entity.getClass(), columnName);
                    if (columnEntry != null) {
                        columnEntry.getField().setAccessible(true);
                        columnEntry.getField().set(entity, entry.getValue());
                    }
                }
            }
            catch (Exception e) {
                throw new PersistenceException("Append Entity MappedColumn for Class: " + entity.getClass().getName() + " failed.", e);
            }
        }
    }

    public static Collection<ColumnEntry> getColumnEntries(Class<?> resultClass) {
        return resultClass2ColumnsMap.get(resultClass).values();
    }

    public static Collection<MappingColumnEntry> getMappingColumnEntries(Class<?> resultClass) {
        List<MappingColumnEntry> mappingColumnEntries = new ArrayList<MappingColumnEntry>();
        Map<String, ColumnEntry> entries = resultClass2ColumnsMap.get(resultClass);
        if (entries != null && !entries.isEmpty()) {
            for (ColumnEntry entry : resultClass2ColumnsMap.get(resultClass).values()) {
                if (entry instanceof MappingColumnEntry) {
                    mappingColumnEntries.add((MappingColumnEntry)entry);
                }
            }
        }
        return mappingColumnEntries;
    }

    public static Collection<ColumnEntry> getNonMappedColumnEntries(Class<?> resultClass) {
        List<ColumnEntry> nonMappedColumnEntries = new ArrayList<ColumnEntry>();
        Map<String, ColumnEntry> entries = resultClass2ColumnsMap.get(resultClass);
        if (entries != null && !entries.isEmpty()) {
            for (ColumnEntry entry : resultClass2ColumnsMap.get(resultClass).values()) {
                if (!(entry instanceof MappingColumnEntry)) {
                    nonMappedColumnEntries.add(entry);
                }
            }
        }
        return nonMappedColumnEntries;
    }

    public static ColumnEntry getColumnEntry(Class<?> resultClass, String columnName) {
        return resultClass2ColumnsMap.get(resultClass).get(columnName);
    }

    public static ColumnEntry getPKColumnEntry(Class<?> resultClass){
        Map<String, ColumnEntry> entries = resultClass2ColumnsMap.get(resultClass);
        for(Map.Entry<String, ColumnEntry> entry : entries.entrySet()){
            ColumnEntry columnEntry = entry.getValue();
            if(columnEntry.isPK()) {
                return columnEntry;
            }
        }
        return null;
    }

}
