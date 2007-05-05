package org.jfox.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.PersistenceException;

import org.jfox.entity.annotation.MappedColumn;
import org.jfox.framework.BaseRuntimeException;
import org.jfox.util.AnnotationUtils;
import org.jfox.util.MethodUtils;

/**
 * 使用 Map 来统一封装数据
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MappedEntity implements EntityObject {
    /**
     * prefix of @MappedColumn
     */
//    public final static String MAPPED_COLUMN_PREFIX = "MAPPED_COLUMN_";

    /**
     * entity interface
     */
    private Class<?> dataObjectInterfClass = null;

    /**
     * 方法名到列名的映射，如果是 MappedColumn Method, Method name =
     * Method_name => Column_name
     */
    private Map<String, String> columnMap = new HashMap<String, String>();

    /**
     * 用来存储查询到的 column=>value 值对
     */
    private Map<String, Object> valueMap = new HashMap<String, Object>();

    MappedEntity(Class<?> doInterface) {
        this.dataObjectInterfClass = doInterface;
        introspect();
    }

    protected void introspect() {
        Method[] columnMethods = AnnotationUtils.getAnnotatedMethods(dataObjectInterfClass, Column.class);
        for (Method columnMethod : columnMethods) {
            Column column = columnMethod.getAnnotation(Column.class);
            columnMap.put(getColumnMapKeyByMethod(columnMethod), column.name());
        }
        Method[] mappedColumnMethods = AnnotationUtils.getAnnotatedMethods(dataObjectInterfClass, MappedColumn.class);
        for (Method columnMethod : mappedColumnMethods) {
            columnMap.put(getColumnMapKeyByMethod(columnMethod), getColumnMapKeyByMethod(columnMethod));
        }
    }

    public void putAll(Map<String, Object> _valueMap) {
        for (Map.Entry<String, Object> entry : _valueMap.entrySet()) {
            // key 转成大写
            setColumnValue(entry.getKey().toUpperCase(), entry.getValue());
        }
    }

    /**
     * 得到 method 对应的在 columnMap 中的 key, columnMap 中的 key，默认是 method.name
     * 但是对于 @MappedColumn 描述的，在 columnMap 的 key 对应的是 MAPPED_COLUMN_ + method.name
     *
     * @param method method
     */
    public static String getColumnMapKeyByMethod(Method method) {
        if(MethodUtils.isGetMethod(method)) {
            if (method.getName().startsWith("is")) {
                return method.getName().substring(2).toUpperCase();
            }
            else {
                return method.getName().substring(3).toUpperCase();
            }
        }
        else if(MethodUtils.isSetMethod(method)) {
            return method.getName().substring(3).toUpperCase();
        }
        else {
            throw new PersistenceException("Method " + method + " is not setter/gett of a Column!");
        }
    }

    public final String getColumnNameByMethod(Method method) {
        String key = getColumnMapKeyByMethod(method);
        if(!columnMap.containsKey(key)) {
            throw new PersistenceException("Could not found column name for method: " + method + "!");
        }
        return columnMap.get(key);
    }

    public final Object getColumnValue(String colName) {
        return valueMap.get(colName.toUpperCase());
    }

    public final void setColumnValue(String colName, Object colValue) {
        valueMap.put(colName.toUpperCase(), colValue);
    }

    public boolean containsColumn(String colName) {
        return valueMap.containsKey(colName);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(dataObjectInterfClass.getSimpleName()).append("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (++i < valueMap.size()) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    /**
     * 根据 DataObject 接口生成 DataObject 实例
     *
     * @param dataObjectIntfClass data object interface class
     */
    public static <T> T newEntityObject(Class<T> dataObjectIntfClass) {
        return newEntityObject(dataObjectIntfClass, new HashMap<String,Object>());
    }

    /**
     * 使用数据 Map 生成动态代理 PO
     *
     * @param entityInterfaceClass entity class
     * @param map                   data map
     */
    public static <T> T newEntityObject(final Class<T> entityInterfaceClass, final Map<String, Object> map) {
        if (!entityInterfaceClass.isInterface()) {
            throw new BaseRuntimeException("Create Entity Object failed, not provide a data object interface, class is: " + entityInterfaceClass);
        }

        final MappedEntity mappedEntity = new MappedEntity(entityInterfaceClass);
        mappedEntity.putAll(map);

        if (entityInterfaceClass.equals(EntityObject.class)) {
//            如果接口为 EntityObject，直接返回 EntityMapper
            return (T)mappedEntity;
        }
        else {
            return newEntityObject(entityInterfaceClass, mappedEntity);
        }
    }

    public static <T> T newEntityObject(final Class<T> entityInterfaceClass, final MappedEntity mappedEntity) {
        List<Class> interfaceClasses = new ArrayList<Class>();
        interfaceClasses.add(entityInterfaceClass);
        if (!EntityObject.class.isAssignableFrom(entityInterfaceClass)) {
            interfaceClasses.add(EntityObject.class);
        }
        return (T)Proxy.newProxyInstance(
                entityInterfaceClass.getClassLoader(),
                interfaceClasses.toArray(new Class[interfaceClasses.size()]),
                new EntityMapperInvocationHandler(mappedEntity));
    }

    public static class EntityMapperInvocationHandler implements InvocationHandler, Serializable {

        private MappedEntity mappedEntity;

        public EntityMapperInvocationHandler(MappedEntity mappedEntity) {
            this.mappedEntity = mappedEntity;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass().equals(EntityObject.class)) {
                return method.invoke(mappedEntity, args);
            }
            else if (method.getDeclaringClass().equals(Object.class)) {
                return method.invoke(mappedEntity, args);
            }
            else if (MethodUtils.isGetMethod(method)) {
                return mappedEntity.getColumnValue(mappedEntity.getColumnNameByMethod(method)); // 应该使用 Method Annotation
            }
            else if (MethodUtils.isSetMethod(method)) {
                // 只有 @Column 描述了匹配的 get 才做 setColumnValue，
                mappedEntity.setColumnValue(mappedEntity.getColumnNameByMethod(method), args[0]);
                return null;
            }
            else {
                return null;
            }
        }
    }

    public Object clone() throws CloneNotSupportedException{
        MappedEntity entity = (MappedEntity)super.clone();
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.putAll(this.valueMap);
        entity.valueMap = valueMap;
        return newEntityObject(dataObjectInterfClass,entity);
    }

    public static void main(String[] args) {

    }
}
