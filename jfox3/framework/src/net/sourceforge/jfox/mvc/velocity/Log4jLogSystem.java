package net.sourceforge.jfox.mvc.velocity;

import org.apache.log4j.Logger;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class Log4jLogSystem implements LogSystem {
    /**
     * log4java logging interface
     */
    protected Logger logger = Logger.getLogger(VelocityEngine.class);

    public Log4jLogSystem() {
    }

    public void init(RuntimeServices rs) throws Exception {

    }

    /**
     * logs messages
     *
     * @param level   severity level
     * @param message complete error message
     */
    public void logVelocityMessage(int level, String message) {
        switch (level) {
            case LogSystem.WARN_ID:
                logger.warn(message);
                break;
            case LogSystem.INFO_ID:
                logger.info(message);
                break;
            case LogSystem.DEBUG_ID:
                logger.debug(message);
                break;
            case LogSystem.ERROR_ID:
                logger.error(message);
                break;
            default:
                logger.debug(message);
                break;
        }
    }

}
