/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

/**
 * 可以为一个独立线程运行的 Component
 * 需要手动的构造 Thread 运行 Component, Framework不会自动启动 Thread
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface RunnableComponent extends Component, Runnable{

    void run();

}
