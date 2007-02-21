package net.sourceforge.jfox.framework.dependent;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public interface Dependence {

    /**
     * 解析 Dependence，并注入到 instance 中
     * 如果 instance 为 null, 则说明是 class level 的依赖
     * @param instance ejb instance
     */
    void inject(Object instance) throws InjectionException;

}
