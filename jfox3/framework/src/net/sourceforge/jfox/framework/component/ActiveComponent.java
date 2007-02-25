package net.sourceforge.jfox.framework.component;

/**
 * 实现了该接口的 Component 会在部署时，立即实例化，实现了该接口，将强制 @Deploy(active=true)
 *
 * 使用 @Annotation 因为不能继承，所以无法强制约束子类型是 Active
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ActiveComponent extends Component {

}
