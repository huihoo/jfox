package net.sourceforge.jfox.framework.dependent;

import java.lang.reflect.Field;

import net.sourceforge.jfox.framework.dependent.Dependence;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.framework.annotation.Constant;
import net.sourceforge.jfox.framework.component.ComponentContext;
import net.sourceforge.jfox.util.ClassUtils;
import net.sourceforge.jfox.util.PlaceholderUtils;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ConstantDependence implements Dependence {

    private Logger logger = Logger.getLogger(ConstantDependence.class);

    private ComponentContext context;
    private Field field;

    public ConstantDependence(ComponentContext componentContext, Field field) {
        this.context = componentContext;
        this.field = field;
    }

    public void inject(Object instance) throws InjectionException {
        Object target = null;

        Constant constant = field.getAnnotation(Constant.class);
        Class<?> filedType = constant.type();
        if (constant.type().equals(Constant.FieldType.class)) {
            // 默认 type
            filedType = field.getType();
        }
        String value = constant.value(); // value 优先，如果有 value 也有 ref，将使用 value
        value = PlaceholderUtils.getInstance().evaluate(value);
        try {
            target = ClassUtils.newObject(filedType, value);
        }
        catch (Exception e) {
            throw new InjectionException("Injection failed, can not construct enject object, type=" + filedType + ", value=" + value, e);
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
