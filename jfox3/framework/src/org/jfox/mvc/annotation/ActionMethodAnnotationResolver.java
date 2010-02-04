package code.google.webactioncontainer.annotation;

import code.google.jcontainer.resolver.MethodAnnotationResolver;
import code.google.jcontainer.resolver.MethodResolveContext;
import code.google.jcontainer.resolver.ResolverException;
import code.google.webactioncontainer.WebActionContainer;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ActionMethodAnnotationResolver implements MethodAnnotationResolver {

    public void resolveMethodAnnotationBeforeConstruct(MethodResolveContext methodResolveContext) throws ResolverException {
        // add action method to WebActionContainer
        ((WebActionContainer)methodResolveContext.getContainer()).addActionMethod(methodResolveContext.getMethod(), (ActionMethod)methodResolveContext.getAnnotation());
    }

    public void resolveMethodAnnotationAfterConstruct(MethodResolveContext methodResolveContext) throws ResolverException {
    }

    public void resolveMethodAnnotationBeforeMethod(MethodResolveContext methodResolveContext) throws ResolverException {

    }

    public void resolveMethodAnnotationAfterMethod(MethodResolveContext methodResolveContext) throws ResolverException {

    }

}
