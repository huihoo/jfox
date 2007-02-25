package net.sourceforge.jfox.framework.component;

import net.sourceforge.jfox.framework.ComponentId;

/**
 * 扩展点接口
 * 扩展点是用来描述一个组件，有一些未知的功能，需要以后的组件和扩展实现
 * 扩展点的好处是：
 * 通过XML描述文件来描述扩展点及扩展，在解析XML部署文件时进行扩展点的关联，
 * 此时实现扩展的组件，还没有实例化，等待真正要用到扩展实现的时候才实例化
 *
 * 扩展具体的调用逻辑由实现ExtentionPointSupportComponent的组件来完成
 *
 *
 * XML example:
 * <extention-point id="extent_point_1">
 *      <extention-property name="class" description="the class of parser"/>
 * </extention>
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ExtentionPoint {

    String getId();

    /**
     * 得到所有定义的参数
     */
    PropertyDefinition[] getPropertiesDefinition();

    /**
     * 关联一个新的 Extention
     * @param extention extion
     */
    void connectExtention(Extention extention);

    /**
     * 根据 id 获得一个扩展
     * @param extentionId 扩展点 id
     */
    Extention getExtention(String extentionId);

    /**
     * 关联了 ExtentionPoint 的 Extention 列表
     */
    Extention[] getExtentions();

    /**
     * 定义该 ExtentionPoint 的 Component
     */
    ComponentId getComponentId();

    public class PropertyDefinition {
        private String name;
        private String description;
    }
}
