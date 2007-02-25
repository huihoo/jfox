package net.sourceforge.jfox.framework.component;

/**
 * Component 注销时的回调接口
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ComponentUnregistration {
    //note: 因为 register 的时候，是 register ComponentMeta，所以无法回调
    //所有只有 unregister 的时候，能回调

     void preUnregister(ComponentContext context);

     void postUnregister();
}
