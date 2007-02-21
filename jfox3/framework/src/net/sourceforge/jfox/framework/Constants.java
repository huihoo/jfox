package net.sourceforge.jfox.framework;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class Constants {

    public static final String DEFAULT_FRAMEOWKR_HOME = ".";
    public static final String FRAMEOWKR_HOME_KEY = "FRAMEWORK_HOME";

    public static final String COMMON_LIB_PATH = "common";

    /**
     * 模块所在的目录
     */
    public static final String DEFAULT_MODULE_DIR = "MODULES";
    public static final String MODULE_DIR_KEY = "MOUDULE_DIR";

    public static final String MODULE_CONFIG_DIR = "conf";
    public static final String MODULE_CONFIG_FILENAME = "module.xml";
    public static final String MOUDULE_CLASS_OUTPUT_PATH = "classes";
    public static final String MOUDULE_CLASS_WEB_OUTPUT_PATH = "WEB-INF/classes";
    public static final String MOUDULE_CLASS_WEB_LIB = "WEB-INF/lib";

    public static final String GLOBAL_PROPERTIES = "global.properties";

    public static String getCommonLibPath(){
        String home = System.getProperty(FRAMEOWKR_HOME_KEY,DEFAULT_FRAMEOWKR_HOME);
        return home + "/" + COMMON_LIB_PATH;
    }

    public static void main(String[] args) {

    }
}
