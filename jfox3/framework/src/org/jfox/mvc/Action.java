/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

/**
 * 所有Action的接口，Action实现Service接口，这样才能被外界所访问
 *
 * WebAction 会注册到 Framework，WebAction 注册的时候，
 * 会将Action添加到 ControllerServlet 的Action列表中
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface Action {

    void execute(ActionContext actionContext) throws Exception;

}
