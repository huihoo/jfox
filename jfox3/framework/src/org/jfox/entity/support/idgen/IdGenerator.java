package org.jfox.entity.support.idgen;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jan 12, 2009 4:21:37 PM
 */
public interface IdGenerator {
    //TODO：所有  Id Generator 实现该接口
    // 由 EntityObject 指定
    String nextId();
}
