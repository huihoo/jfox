package jfox.test.ejb3.entity;

import org.jfox.entity.mapping.EntityFactory;
import org.jfox.entity.mapping.MappingColumnEntry;
import org.junit.Test;

import java.util.Collection;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-1-31 17:33:43
 */
public class EntityFactoryTest {

    @Test
    public void testToMany() {
        EntityFactory.introspectResultClass(Order.class);
        Collection<MappingColumnEntry> mappingColumnEntries = EntityFactory.getMappingColumnEntries(Order.class);
        for(MappingColumnEntry mappingColumnEntry : mappingColumnEntries){
            System.out.println(mappingColumnEntry.getTargetEntity());
        }
    }

    public static void main(String[] args) {

    }
}
