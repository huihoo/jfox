/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.timer;

import java.util.Date;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimedObject;
import javax.ejb.TimerService;
import javax.annotation.Resource;

@Stateless(name = "timer.ExampleTimerBean")
@Remote
public class ExampleTimerBean implements ExampleTimer, TimedObject {

    @Resource
    private SessionContext ctx;

    @Resource
    TimerService timerService;


    public void scheduleTimer(long milliseconds) {
        ctx.getTimerService().createTimer(new Date(new Date().getTime() + milliseconds), "Hello World");
        timerService.createTimer(new Date(new Date().getTime() + milliseconds), "Hello World2");
    }

    @Timeout
    public void timeoutHandler(Timer timer) {
        System.out.println("---------------------");
        System.out.println("* Received Timer event: " + timer.getInfo());
        System.out.println("---------------------");
        timer.cancel();
    }

    public void ejbTimeout(Timer timer) {
        System.out.println("---------------------");
        System.out.println("* Received interface Timer event : " + timer.getInfo());
        System.out.println("---------------------");
        timer.cancel();
    }
}
