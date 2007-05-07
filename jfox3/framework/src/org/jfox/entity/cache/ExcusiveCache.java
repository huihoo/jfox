/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.cache;

/**
 * //TODO: 独占式 Cache，在一个Cache对象被使用时，不允许其它线程使用
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ExcusiveCache extends SimpleCache {

    public ExcusiveCache(String name, CacheConfig config) {
        super(name, config);
    }

    public static void main(String[] args) {

    }
}
