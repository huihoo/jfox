package net.sourceforge.jfox.entity;

import javax.persistence.NamedNativeQuery;

import net.sourceforge.jfox.framework.component.Component;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface EntityManagerFactoryBuilder extends Component {

    void registerNamedQuery(NamedNativeQuery namedNativeQuery, Class<?> definedClass);

    NamedSQLTemplate getNamedQuery(String name);

    Document getPersistenceXMLDocument();
}
