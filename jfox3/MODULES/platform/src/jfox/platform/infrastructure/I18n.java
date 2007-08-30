package jfox.platform.infrastructure;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class I18n {

    private final static String RESOURCE_BUNDLE_NAME = "iservicedesk";

    private final static Map<Locale, ResourceBundle> locale2ResourceBundle = new HashMap<Locale, ResourceBundle>(4);
    static {
        ResourceBundle en_US_Bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, Locale.US);
        ResourceBundle zh_CN_Bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, Locale.SIMPLIFIED_CHINESE);

        locale2ResourceBundle.put(Locale.US, en_US_Bundle);
        locale2ResourceBundle.put(Locale.SIMPLIFIED_CHINESE, zh_CN_Bundle);
    }

    public static String getString(Locale locale, String resourceName){
        return locale2ResourceBundle.get(locale).getString(resourceName);
    }

}
