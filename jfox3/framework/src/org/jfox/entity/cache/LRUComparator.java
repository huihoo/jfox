/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.cache;

import java.util.Comparator;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class LRUComparator implements Comparator<CachedObject> {

    public int compare(CachedObject thisCachedObject, CachedObject thatCachedObject) {
        return thisCachedObject.getLastAccessTime() < thatCachedObject.getLastAccessTime() ? -1
                : thisCachedObject.getLastAccessTime() == thatCachedObject.getLastAccessTime() ?
                (thisCachedObject.getCreateTime() < thatCachedObject.getCreateTime() ? -1 : (thisCachedObject.getCreateTime() == thatCachedObject.getCreateTime() ? 0 : 1))
                : 1;
    }

    public static void main(String[] args) {

    }
}
