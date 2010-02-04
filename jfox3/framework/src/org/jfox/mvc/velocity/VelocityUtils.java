/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer.velocity;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.EventHandler;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class VelocityUtils {

    private static VelocityEngine ve = new VelocityEngine();

    static {
        /* first, we init the runtime engine.  Defaults are fine. */

        try {

/*
 *  configure the engine.  In this case, we are using
 *  ourselves as a logger (see logging examples..)
 */

//            ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);

/*
 *  initialize the engine
 */

            ve.init();
        }
        catch (Exception e) {
            throw new RuntimeException("Problem initializing Velocity : " + e);
        }
    }


    private VelocityUtils() {
    }

    /**
     * 替换 placeholder
     *
     * @param template   输入的模版
     * @param mapContext map context
     * @return 替换之后的内容
     */
    public static String evaluate(String template, Map<String, Object> mapContext) {
        VelocityContext context = new VelocityContext();
        for (Map.Entry entry : mapContext.entrySet()) {
            context.put((String)entry.getKey(), entry.getValue());
        }

        return evaluate(template, context);
    }

    public static String evaluate(String template, VelocityContext context) {

        try {
            StringWriter sw = new StringWriter();
            ve.evaluate(context, sw, VelocityUtils.class.getSimpleName(), template);
            return sw.toString();
        }
        catch (Exception pee) {
            pee.printStackTrace();
            return template;
        }
    }

    public static String evaluate(String template, Map<String, Object> mapContext, EventHandler... eventHandlers) {
        VelocityContext context = new VelocityContext();
        for (Map.Entry entry : mapContext.entrySet()) {
            context.put((String)entry.getKey(), entry.getValue());
        }
        EventCartridge ec = new EventCartridge();

        for(EventHandler eventHandler : eventHandlers) {
            ec.addEventHandler(eventHandler);
        }
        ec.attachToContext(context);
        return evaluate(template, context);
    }


    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("time", System.currentTimeMillis());
        String out = VelocityUtils.evaluate("Hello, World! time is: $time", map);
        System.out.println(out);
    }
}
