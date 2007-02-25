package net.sourceforge.jfox.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 占位符替换工具类
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class PlaceholderUtils {

    private static final Logger logger = Logger.getLogger(PlaceholderUtils.class);

    /**
     * 全局属性
     */
    private Properties globalProperties = new Properties();

    private static List<String> loadedProperties = new ArrayList<String>();

    private static final PlaceholderUtils instance = new PlaceholderUtils();

    private PlaceholderUtils() {
    }

    private void loadProperties(Properties prop) {
        globalProperties.putAll(prop);
    }

    public static PlaceholderUtils getInstance() {
        return instance;
    }

    /**
     * 装载一个全局配置文件
     *
     * @param filename properties file name
     * @return PlaceHolderUtil
     * @throws IOException if failed to load property file
     */
    public static PlaceholderUtils loadGlobalProperty(String filename) throws IOException {
        if (!loadedProperties.contains(filename)) {
            InputStream in = PlaceholderUtils.class.getClassLoader().getResourceAsStream(filename);
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                instance.loadProperties(prop);
                loadedProperties.add(filename);
            }
            else {
                throw new FileNotFoundException(filename);
            }
        }
        return instance;
    }

    /**
     * 替换 placeholder
     *
     * @param template 输入的模版
     * @return 替换之后的内容
     */
    public String evaluate(String template) {
        return VelocityUtils.evaluate(template,(Map)globalProperties);
    }

    /**
     * 从一个文件中替换，返回替换后的文本
     *
     * @param url file url
     * @return 替换后的文本
     * @throws IOException if failed to load file
     */
    public String evaluate(URL url) throws IOException {
        String content = IOUtils.toString(url.openStream());
        return evaluate(content);
    }

    public static void main(String[] args) throws Exception {
        PlaceholderUtils pu = loadGlobalProperty("global.properties");
        System.out.println(pu.evaluate("I use database type is ${db_type}!"));

    }
}
