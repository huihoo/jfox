package org.jfox.entity.support.idgen;

import java.util.UUID;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jan 12, 2009 4:33:10 PM
 */
public class UUIDIdGenerator implements IdGenerator{

    public String nextId() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {

    }
}
