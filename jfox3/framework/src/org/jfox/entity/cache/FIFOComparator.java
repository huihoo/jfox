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
public class FIFOComparator implements Comparator<CachedObject> {

    public int compare(CachedObject thisCachedObject, CachedObject thatCachedObject) {
        return Long.valueOf(thisCachedObject.getCreateTime()).compareTo(thisCachedObject.getCreateTime());
    }

    public static void main(String[] args) {

    }
}
