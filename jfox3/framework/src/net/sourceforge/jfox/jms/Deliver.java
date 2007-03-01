/* JFox, the OpenSource J2EE Application Server
 *
 * Distributable under GNU LGPL license by gun.org
 * more details please visit http://www.huihoo.org/jfox
 */

package net.sourceforge.jfox.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.jms.Destination;

/**
 * every destination has its own DeliveryManager
 * DeliveryManager response store the messages, check the client alive and deliver message to it
 *
 * @author <a href="mailto:young_yy@hotmail.com">Young Yang</a>
 */

public class Deliver {
    private static Destination dest;

    /**
     * messages waiting for delivery
     */
    private List messages = new ArrayList();

    /**
     * messages have sent but not ackowneledge
     */
    private HashMap unacknowledgedMessages = new HashMap();


    public static void main(String[] args) {

    }
}
