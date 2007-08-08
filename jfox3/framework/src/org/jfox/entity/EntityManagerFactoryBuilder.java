/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import javax.persistence.NamedNativeQuery;

import org.jfox.framework.component.Component;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface EntityManagerFactoryBuilder extends Component {

    void registerNamedQuery(NamedNativeQuery namedNativeQuery, Class<?> definedClass);

    NamedSQLTemplate getNamedQuery(String name, String dbType);

    Document getPersistenceXMLDocument();
}
