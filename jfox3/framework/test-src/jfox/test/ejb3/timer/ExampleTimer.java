/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.timer;

public interface ExampleTimer {
    /**
     * EJB 方法，用来提交定时任务
     */
    void scheduleTimer(long milliseconds);
}
