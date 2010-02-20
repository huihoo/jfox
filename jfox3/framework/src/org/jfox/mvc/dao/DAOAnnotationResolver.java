package code.google.webactioncontainer.dao;

import code.google.jcontainer.ContainerFactory;
import code.google.jcontainer.NoSuchComponentRuntimeException;
import code.google.jcontainer.resolver.FieldAnnotationResolver;
import code.google.jcontainer.resolver.FieldResolveContext;
import code.google.jcontainer.resolver.ResolverException;
import code.google.webactioncontainer.WebContextLoader;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class DAOAnnotationResolver implements FieldAnnotationResolver{

    public void resolveFieldAnnotationAfterConstruct(FieldResolveContext fieldResolveContext) throws ResolverException {
        DAO daoAnnotation = (DAO)fieldResolveContext.getAnnotation();
        Field field =  fieldResolveContext.getField();
        DAOContainer daoContainer = (DAOContainer)WebContextLoader.getContainerFactory().getContainer("DAOContainer");
        Object daoComponent;
        String daoName = daoAnnotation.name();
        if(daoName != null && !daoName.trim().equals("")) {
            daoComponent = daoContainer.getComponent(daoAnnotation.name());
            if(daoComponent == null) {
                throw new NoSuchComponentRuntimeException("name=" + daoName.trim());
            }
        }
        else {
            Class componentClass = field.getType();
            daoComponent = daoContainer.getComponent(componentClass);
            if(daoComponent == null) {
                throw new NoSuchComponentRuntimeException("class=" + componentClass.getName());
            }
        }
        try {
            field.setAccessible(true);
            field.set(fieldResolveContext.getComponent(), daoComponent);
        }
        catch (Exception e) {
            throw new ResolverException("Field to resolve DAO field: " + field);
        }
        
    }

    public void resolveFieldAnnotationBeforeConstruct(FieldResolveContext fieldResolveContext) throws ResolverException {

    }

}
