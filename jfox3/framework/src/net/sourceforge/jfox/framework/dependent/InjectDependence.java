package net.sourceforge.jfox.framework.dependent;

import java.lang.reflect.Field;
import java.util.Collection;

import net.sourceforge.jfox.framework.ComponentId;
import net.sourceforge.jfox.framework.annotation.Inject;
import net.sourceforge.jfox.framework.component.Component;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.framework.component.ComponentNotExportedException;
import net.sourceforge.jfox.framework.component.ComponentNotFoundException;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class InjectDependence implements Dependence {

    private Logger logger = Logger.getLogger(InjectDependence.class);

    private ComponentContext context;
    private Field field;

    public InjectDependence(ComponentContext componentContext, Field field) {
        this.context = componentContext;
        this.field = field;
    }

    public void inject(Object instance) throws InjectionException {
        Inject inject = field.getAnnotation(Inject.class);
        Class<?> fieldType = inject.type();
        if (inject.type().equals(Inject.FieldType.class)) {
            // 默认 type
            fieldType = field.getType();
        }
        if(!Component.class.isAssignableFrom(fieldType)){
            throw new InjectionException("Only Type implements Component interface can be injected.");
        }


        Object target = null;

        String value = inject.id(); // value 优先，如果有 value 也有 ref，将使用 value
        if (value.trim().length() == 0) { // 自动发现
            Collection<? extends Component> components = context.findComponentBySuper(fieldType.asSubclass(Component.class));
            if (components.isEmpty()) {
                logger.warn("Can not find component implement interface: " + fieldType.getName());
            }
            else {
                if (components.size() > 1) {
                    logger.warn("More than one component found implement interface: " + fieldType.getName() + ", use the first one!");
                }
                target = components.iterator().next();
            }
        }
        else { // 根据 id 来查找
            try {
                target = context.getComponentById(new ComponentId(value));
            }
            catch(ComponentNotFoundException e) {
                logger.warn("Can not found component with id: " + value);
            }
            catch(ComponentNotExportedException e) {
                logger.warn("Canot reference a not exportd component with id: " + value);
            }
        }
        try {
            field.setAccessible(true);
            field.set(instance, target);
        }
        catch (IllegalAccessException e) {
            throw new InjectionException("Failed to enject field: " + field.getName(), e);
        }

    }

    public static void main(String[] args) {

    }
}
