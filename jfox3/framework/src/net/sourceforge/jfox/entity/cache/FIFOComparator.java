package net.sourceforge.jfox.entity.cache;

import java.util.Comparator;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class FIFOComparator implements Comparator<CachedObject> {

    public int compare(CachedObject thisCachedObject, CachedObject thatCachedObject) {
        return Long.valueOf(thisCachedObject.getCreateTime()).compareTo(thisCachedObject.getCreateTime());
    }

    public static void main(String[] args) {

    }
}
