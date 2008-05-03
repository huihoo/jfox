package org.jfox.entity.dao;

import org.jfox.entity.mapping.ColumnEntry;
import org.jfox.entity.mapping.EntityFactory;

import javax.persistence.Table;
import java.util.Collection;

/**
 * TODO: SQL 生成器， 生成默认增删改查语句
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Apr 27, 2008 10:58:31 AM
 */
public class SQLGenerator {

    /**
     * build create sql
     * @param entityClass
     * @return sql velocity template
     */
    public static String buildInsertSQL(Class<?> entityClass){
        EntityFactory.introspectResultClass(entityClass);
        String tableName = getTableName(entityClass);
        StringBuffer sql = new StringBuffer("INSERT INTO ").append(tableName).append(" (");
        StringBuffer sql2 = new StringBuffer(" VALUES (");
        Collection<ColumnEntry> nonMappedColumnEntries =  EntityFactory.getNonMappedColumnEntries(entityClass);
        int colIndex = 0;
        for(ColumnEntry entry : nonMappedColumnEntries){
            String colName = entry.getName();
            if(colIndex > 0) {
                sql.append(", ");
                sql2.append(", ");
            }
            sql.append(colName.toUpperCase());
            sql2.append("$").append(tableName).append(".").append(entry.getField().getName());
            colIndex++;
        }
        sql.append(")");
        sql2.append(")");
        sql.append(sql2);
        return sql.toString();
    }

    /**
     * build delete sql
     * @param entityClass
     * @return sql velocity template
     */
    public static String buildDeleteByIdSQL(Class<?> entityClass){
        EntityFactory.introspectResultClass(entityClass);
        String tableName = getTableName(entityClass);
        StringBuffer sql = new StringBuffer("DELETE FROM ").append(tableName).append(" WHERE ");
        String pkColumnName = "ID"; // default use ID column as PK
        ColumnEntry pkColumnEntry = EntityFactory.getPKColumnEntry(entityClass);
        if(pkColumnEntry != null) {
            pkColumnName = pkColumnEntry.getName().toUpperCase();
        }
        sql.append(pkColumnName).append(" = ").append("$").append(pkColumnName);
        return sql.toString();
    }

public static String buildDeleteByColumnSQL(Class entityClass, String... columns) {
        EntityFactory.introspectResultClass(entityClass);
        String tableName = getTableName(entityClass);
        StringBuffer sql = new StringBuffer("DELETE FROM ").append(tableName).append(" WHERE ");
        int colIndex = 0;
        for(String column : columns) {
            if(colIndex > 0) {
                sql.append(" AND ");
            }
            sql.append(column.toUpperCase()).append(" = $").append(column.toUpperCase());
            colIndex++;
        }
        return sql.toString();
        
    }

    public static String buildUpdateSQL(Class<?> entityClass){
        EntityFactory.introspectResultClass(entityClass);
        String tableName = getTableName(entityClass);
        StringBuffer sql = new StringBuffer("UPDATE ").append(tableName).append(" SET ");

        Collection<ColumnEntry> nonMappedColumnEntries =  EntityFactory.getNonMappedColumnEntries(entityClass);
        int colIndex = 0;
        for(ColumnEntry entry : nonMappedColumnEntries){
            if(!entry.isPK()) {
                String colName = entry.getName();
                if(colIndex > 0) {
                    sql.append(", ");
                }
                sql.append(colName.toUpperCase()).append( " = $").append(tableName).append(".").append(entry.getField().getName());
                colIndex++;
            }
        }

        String pkColumnName = "ID";
        String pkColumnFieldName = "id"; // default use ID column as PK
        ColumnEntry pkColumnEntry = EntityFactory.getPKColumnEntry(entityClass);
        if(pkColumnEntry != null) {
            pkColumnName = pkColumnEntry.getName();
            pkColumnFieldName = pkColumnEntry.getField().getName();
        }
        sql.append(" WHERE ").append(pkColumnName.toUpperCase()).append(" = ").append("$").append(tableName).append(".").append(pkColumnFieldName);
        return sql.toString();
    }

    /**
     * buid select SQL by column
     * @param entityClass
     * @param columns
     * @return
     */
    public static String buildSeleteSQLByColumn(Class<?> entityClass, String... columns){
        EntityFactory.introspectResultClass(entityClass);
        String tableName = getTableName(entityClass);
        StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tableName).append(" WHERE ");
        int colIndex = 0;
        for(String column : columns) {
            if(colIndex > 0) {
                sql.append(" AND ");
            }
            sql.append(column.toUpperCase()).append(" = $").append(column.toUpperCase());
            colIndex++;
        }
        return sql.toString();
    }

    /**
     * build select sql by id column
     * @param entityClass
     * @return
     */
    public static String buildSeleteSQLById(Class<?> entityClass){
        EntityFactory.introspectResultClass(entityClass);
        String tableName = getTableName(entityClass);
        StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tableName).append(" WHERE ");
        String pkColumnName = "ID"; // default use ID column as PK
        ColumnEntry pkColumnEntry = EntityFactory.getPKColumnEntry(entityClass);
        if(pkColumnEntry != null) {
            pkColumnName = pkColumnEntry.getName().toUpperCase();
        }
        sql.append(pkColumnName.toUpperCase()).append(" = $").append(pkColumnName.toUpperCase());
        return sql.toString();
    }

    /**
     * 根据 entityClass 获取 Table name 
     */
    public static String getTableName(Class<?> entityClass) {
        String tableName = entityClass.getSimpleName().toUpperCase();
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if(table.name().trim().length() > 0) {
                tableName = table.name();
            }
        }
        return tableName;
    }

    public static void main(String[] args) {
        
    }
}
