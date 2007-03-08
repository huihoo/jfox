package org.jfox.example.ejb3.timer;

import java.util.Date;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimedObject;
import javax.annotation.Resource;

@Stateless(name = "timer.ExampleTimerBean")
@Remote
public class ExampleTimerBean implements ExampleTimer, TimedObject {

    @Resource
    private SessionContext ctx;

    public void scheduleTimer(long milliseconds) {
        ctx.getTimerService().createTimer(new Date(new Date().getTime() + milliseconds), "Hello World");
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
