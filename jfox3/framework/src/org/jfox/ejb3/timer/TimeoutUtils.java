package org.jfox.ejb3.timer;

import java.lang.reflect.Method;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.EJBException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TimeoutUtils {

    private static final Method TimeOut;

    static {
        try {
            TimeOut = TimedObject.class.getMethod("ejbTimeout", new Class[]{Timer.class});
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }
    }

    public static Method getTimeoutMethod(){
        return TimeOut;
    }
}
