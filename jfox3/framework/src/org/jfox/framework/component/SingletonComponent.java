package org.jfox.framework.component;

/**
 * 实现了该接口的 Component 会在部署时，无论是否 @Server(singleton=false)，都只有一个实例
 *
 * 使用 @Annotation 因为不能继承，所以无法强制约束子类型是 singleton
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface SingletonComponent extends Component {

}
