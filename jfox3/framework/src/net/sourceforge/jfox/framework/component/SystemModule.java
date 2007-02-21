package net.sourceforge.jfox.framework.component;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ArrayList;

import net.sourceforge.jfox.framework.Framework;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class SystemModule extends Module {

    public static final String name = "__SYSTEM_MODULE__";

    public SystemModule(Framework framework) throws ModuleResolvedFailedException {
        super(framework, null);
    }

    protected ModuleClassLoader initModuleClassLoader() {
        // 覆盖 getResource，以便能够正确检索到 resource
        return new ModuleClassLoader(this) {


            protected URL[] getASMClasspathURLs() {
                URL[] urls = ((URLClassLoader)SystemModule.class.getClassLoader()).getURLs();
                // 只返回含有 Component 类的路径
                List<URL> appURLs = new ArrayList<URL>();
                for (URL url : urls) {
                    URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, null);
                    URL testURL = urlClassLoader.getResource(Component.class.getName().replace(".", "/") + ".class");
                    if (testURL != null) {
                        appURLs.add(url);
                    }
                }
                return appURLs.toArray(new URL[appURLs.size()]);
            }

            public URL getResource(String name) {
                // parent 是 ClassLoaderRepository
                return getParent().getResource(name);
            }
        };
    }

    protected void resolve() throws ModuleResolvedFailedException {
        setName(name);
        setDescription("System Module");
        setPriority(-1);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return super.getDescription();
    }

    /**
     * SystemModule的 classpath 已经制定在启动 classpath中，无须再添加
     */
    public URL[] getClasspathURLs() {
        return new URL[0];
    }

    public URL getDescriptorURL() {
        return null;
    }

    public static void main(String[] args) {

    }
}
