package org.jfox.entity.mapping;

import java.lang.reflect.Field;

/**
 * \@Column field
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 2, 2008 2:16:58 PM
 */
public class ColumnEntry {
    private String name; // column name
    private Field field; // column mapped field

    private boolean isPK = false;

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public boolean isPK() {
        return isPK;
    }

    public boolean isMappedColumn() {
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setPK(boolean PK) {
        isPK = PK;
    }
}
