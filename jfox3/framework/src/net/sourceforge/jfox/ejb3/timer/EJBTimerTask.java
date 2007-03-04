package net.sourceforge.jfox.ejb3.timer;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

import net.sourceforge.jfox.ejb3.EJBObjectId;
import net.sourceforge.jfox.ejb3.SimpleEJB3Container;

/**
 * @author <a href="mailto:yangyong@ufsoft.com.cn">Young Yang</a>
 */

public class EJBTimerTask implements Timer, TimerHandle, Runnable {

    private EJBObjectId ejbObjectId;

    private final List<Method> timeoutMethods = new ArrayList<Method>();

    private Serializable info;

    private SimpleEJB3Container.EJBContainerTimerService timerService;

    /**
     * Scheduled future
     */
    private ScheduledFuture future;

    public EJBTimerTask(SimpleEJB3Container.EJBContainerTimerService timerService, Serializable info) {
        this.timerService = timerService;
        this.info = info;
    }

    public EJBObjectId getEjbObjectId() {
        return ejbObjectId;
    }

    public void setEjbObjectId(EJBObjectId ejbObjectId) {
        this.ejbObjectId = ejbObjectId;
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

    public void addTimeoutMethod(Method[] timeoutMethods){
        this.timeoutMethods.addAll(Arrays.asList(timeoutMethods));
    }

    public Method[] getTimeoutMethods(){
        return timeoutMethods.toArray(new Method[timeoutMethods.size()]);
    }

    public void run() {
        // 执行 @Timeout 方法
        timerService.invokeTimeout(this);
    }

    public static void main(String[] args) {

    }
}

