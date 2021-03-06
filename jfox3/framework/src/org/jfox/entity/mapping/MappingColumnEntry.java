package org.jfox.entity.mapping;

/**
 * \@MappingColumn field
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 2, 2008 2:17:25 PM
 */
public class MappingColumnEntry extends ColumnEntry{
    private String columnDefinition;
    //TODO: 没什么用了，直接用 resultMap 做 query.setParameter，必定包含了 referencedColumnName
    private String referencedColumnName;

    private boolean toMany = false;
    /**
     * 在 OneToMany, ManyToMany，要由 OneToMany.tagetEntity 指定 resultClass, 或者取 List Set 的泛型
     */
    private Class targetEntity;

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public boolean isMappedColumn() {
        return true;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public Class<?> getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Class targetEntity) {
        this.targetEntity = targetEntity;
    }

    public boolean isToMany() {
        return toMany;
    }

    public void setToMany(boolean toMany) {
        this.toMany = toMany;
    }
}
