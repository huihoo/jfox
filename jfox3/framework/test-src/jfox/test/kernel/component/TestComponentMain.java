package jfox.test.kernel.component;

import org.jfox.framework.Framework;
import org.jfox.framework.component.Module;
import org.jfox.framework.component.ComponentMeta;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class TestComponentMain {

    public static void main(String[] args) throws Exception {
        Framework framework = new Framework();
        Module module = framework.getSystemModule();
        ComponentMeta meta = module.loadComponent(TestComponentImpl.class);
//        ComponentMeta meta = new ComponentMeta(module,TestComponentImpl.class);
//        module.registerComponent(meta);
        TestComponent testComponent = (TestComponent)meta.getComponentInstance();
        testComponent.sayHello();
    }
}
