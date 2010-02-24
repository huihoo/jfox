package code.google.webactioncontainer.dao;

import javax.persistence.Entity;

import code.google.jcontainer.AbstractContainer;
import code.google.jcontainer.AnnotationRepository;
import code.google.jcontainer.annotation.Container;

import code.google.webactioncontainer.WebContextLoader;
import org.nativejpa.EntityManagerFactoryBuilder;
import org.nativejpa.query.EntityHolder;


/**
 * Component to hold all DataAccessObject components
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Container(name = "NativeJPADAOContainer",
        componentType = DataAccessObject.class
)
public class NativeJPADAOContainer extends AbstractContainer{

    @Override
    protected void init(AnnotationRepository annotationRepository) {
        super.init(annotationRepository);
        //init NativeJPA EntityManagerFactoryBuild, transfer WebActionContainer's AnnotationRepository to it
        EntityManagerFactoryBuilder entityManagerFactoryBuilder = EntityManagerFactoryBuilder.getInstance();
        entityManagerFactoryBuilder.setEntityHolder(new EntityHolder() {
            public Class<?>[] getEntityClasses() {
                return WebContextLoader.getContainerFactory().getAnnotationRepository().getAnnotatedClasses(Entity.class);
            }
        });

    }

}