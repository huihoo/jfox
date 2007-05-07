/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
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
            TimeOut = TimedObject.class.getMethod("ejbTimeout", Timer.class);
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
