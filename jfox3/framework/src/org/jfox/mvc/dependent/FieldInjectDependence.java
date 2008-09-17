/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc.dependent;

import org.apache.log4j.Logger;
import org.jfox.framework.ComponentId;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.component.Component;
import org.jfox.framework.component.ComponentNotExportedException;
import org.jfox.framework.component.ComponentNotFoundException;
import org.jfox.framework.dependent.Dependence;
import org.jfox.framework.dependent.InjectionException;
import org.jfox.mvc.ActionBucket;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FieldInjectDependence implements Dependence {

    private Logger logger = Logger.getLogger(FieldInjectDependence.class);

    private ActionBucket actionBucket;
    private Field field;

    public FieldInjectDependence(ActionBucket actionBucket, Field field) {
        this.actionBucket = actionBucket;
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
            throw new InjectionException("Can not inject field: " + field + ", because it's type " + fieldType.getName() + " not extends " + Component.class.getName());
        }

        Object target = null;

        String value = inject.id(); // value 优先，如果有 value 也有 ref，将使用 value
        if (value.trim().length() == 0) { // 自动发现
            Collection<? extends Component> components = actionBucket.getModule().findComponentsByInterface(fieldType.asSubclass(Component.class));
            if (components.isEmpty()) {
                logger.warn("Can not find component implement interface: " + fieldType.getName() + " for field: " + field);
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
                target = actionBucket.getModule().getComponent(new ComponentId(value));
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