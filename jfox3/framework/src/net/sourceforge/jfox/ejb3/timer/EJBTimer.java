package net.sourceforge.jfox.ejb3.timer;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimedObject;

import net.sourceforge.jfox.ejb3.SimpleEJB3Container;

/**
 * @author <a href="mailto:yangyong@ufsoft.com.cn">Young Yang</a>
 */

public class EJBTimer implements Timer, TimerHandle, Runnable {
    public static final Method TimeOut;
    static {
        try {
            TimeOut = TimedObject.class.getMethod("ejbTimeout", new Class[]{Timer.class});
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }
    }

    private String ejbName;

    private Serializable info;

    private SimpleEJB3Container.EJBContainerTimerService timerService;

    /**
     * Scheduled future
     */
    private ScheduledFuture future;

    public EJBTimer(Serializable info) {
        this.info = info;
    }

    public void setEJBName(String ejbName) {
        this.ejbName = ejbName;
    }

    public String getEJBName(){
        return this.ejbName;
    }

    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }

    public long getTimeRemaining() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return future.getDelay(TimeUnit.MILLISECONDS);
    }

    public Date getNextTimeout() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return new Date(System.currentTimeMillis() + future.getDelay(TimeUnit.MILLISECONDS));
    }

    public Serializable getInfo() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return info;
    }

    public TimerHandle getHandle() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return this;
    }

    public Timer getTimer() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return this;
    }

    /**
     * need remove from EJBTimerService's timers
     */
    public void cancel() {
        future.cancel(false);
    }

    public Method[] getTimeoutMethods(){
        return null;
    }

    public void run() {
        // 找到 @Timeout 方法
        timerService.timeout(this);
    }

    public static void main(String[] args) {

    }
}

