package jfox.test.kernel.component;

import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ComponentContext;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id="TestComponent")
public class TestComponentImpl implements TestComponent {

    public void sayHello() {
        log("Hello,World, :)!");
    }

    public void postContruct(ComponentContext componentContext) {
        log("postInstantiated!");
    }

    public void postPropertiesSet() {
        log("postPropertiesSet");
    }

    private void log(String msg){
        System.out.println("[" + this.getClass().getSimpleName() + "] " + msg);
    }

    public static void main(String[] args) {

    }
}
