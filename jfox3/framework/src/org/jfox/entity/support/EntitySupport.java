package org.jfox.entity.support;

import org.jfox.entity.support.idgen.TimebasedIdGenerator;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class EntitySupport implements Serializable, Comparable<EntitySupport> {
    
    /**
     * 每个表必须有ID字段，且作为 Primary Key
     */
    @Column(name = "ID")
    @Id
    private long id = 0;

    public EntitySupport() {
        // 使用PKgen生成id
        id = nextId();
    }

    /**
     * 获得 Id，like: 2006121916471910560
     */
    @Id
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    /**
     * 用来排序
     *
     * @param thatEntity entity to be compared
     */
    public int compareTo(EntitySupport thatEntity) {
        return new Long(this.getId()).compareTo(thatEntity.getId());
    }

    /**
     * 根据 @Entity 得到 Table Name
     */
    public String getTableName() {
        String tableName = this.getClass().getSimpleName();
        if (this.getClass().isAnnotationPresent(Table.class)) {
            Table table = this.getClass().getAnnotation(Table.class);
            if(table.name().trim().length() > 0) {
            tableName = table.name();
            }
        }
        return tableName;
    }

    /**
     * 转成 Map，以便方便的生成 JSON 对象
     * 子类可以覆盖该方法，加入一些自定义的 key/value，用于页面显示
     */
    public Map<String, Object> convertToMap() {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        Field[] fields = getAllColumnFields(this.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String columnName = field.getAnnotation(Column.class).name();
                valueMap.put(columnName, field.get(this));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return valueMap;
    }

    /**
     * 取得clz 所有的 Field
     *
     * @param clazz class
     */
    protected static Field[] getAllColumnFields(Class clazz) {
        List<Field> columnFields = new ArrayList<Field>();
        Class[] superClasses = getAllSuperclasses(clazz);
        for (Class superClass : superClasses) {
            for (Field field : superClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    columnFields.add(field);
                }
            }
        }
        return columnFields.toArray(new Field[columnFields.size()]);
    }

    protected static Class[] getAllSuperclasses(Class cls) {
        if (cls == null) {
            return new Class[0];
        }
        List<Class> classList = new ArrayList<Class>();
        classList.add(cls);
        Class superClass = cls.getSuperclass();
        while (superClass != null && !superClass.equals(Object.class)) { // java.lang.Object 不算为超类
            classList.add(superClass);
            superClass = superClass.getSuperclass();
        }
        Collections.reverse(classList); // reverse，以保证子类覆盖超类
        return classList.toArray(new Class[classList.size()]);
    }

    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof EntitySupport)) {
            return false;
        }
        return ((EntitySupport)obj).getId() == this.getId();
    }

    public String toString() {
        return getClass().getName() + "@" + getId();
    }

    protected long nextId() {
        return TimebasedIdGenerator.getInstance().nextLongId();
    }
}