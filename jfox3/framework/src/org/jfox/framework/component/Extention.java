package org.jfox.framework.component;

import org.jfox.framework.ComponentId;

/**
 * 实现某个ExtentionPoint的扩展
 * 扩展在解析XML描述文件时，和ExtentionPoint关联
 *
 * XML example:
 * <extention id="extention_1" point-id="extent_point_1">
 *      <extention-property name="class" value="java.lang.Object"/>
 * </extention>
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface Extention {

    /**
     * 获得一个属性值
     * @param name 属性名称
     */
    String getProperty(String name);

    void addProperty(String name, String value);

    ComponentId getComponentId();

    ExtentionPoint getExtentionPoint();

}
