/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用 Map 来统一封装数据
 * 如果 Query 没有指定 ResultClass，则默认使用 MappedEntity
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class MappedEntity implements Serializable {

    /**
     * 用来存储查询到的 column=>value 值对
     */
    private Map<String, Object> valueMap = new HashMap<String, Object>();

    public static MappedEntity newMappedEntity(final Map<String, Object> map) {
        return new MappedEntity(map);
    }

    MappedEntity(Map<String, Object> _valueMap) {
        putAll(_valueMap);
    }

    void putAll(Map<String, Object> _valueMap) {
        for (Map.Entry<String, Object> entry : _valueMap.entrySet()) {
            // key 转成大写
            setColumnValue(entry.getKey().toUpperCase(), entry.getValue());
        }
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
        sb.append(this.getClass().getSimpleName()).append("{");
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

    public Object clone() throws CloneNotSupportedException{
        MappedEntity entity = (MappedEntity)super.clone();
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.putAll(this.valueMap);
        entity.valueMap = valueMap;
        return entity;
    }

    public Map<String, Object> getValueMap() {
        return Collections.unmodifiableMap(valueMap);
    }

    public static void main(String[] args) {

    }
}
