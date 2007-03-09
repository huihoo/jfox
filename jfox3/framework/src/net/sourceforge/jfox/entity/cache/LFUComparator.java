package net.sourceforge.jfox.entity.cache;

import java.util.Comparator;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class LFUComparator implements Comparator<CachedObject> {

    public int compare(CachedObject thisCachedObject, CachedObject thatCachedObject) {
        return thisCachedObject.getAccessCount() < thatCachedObject.getAccessCount() ? -1
                : thisCachedObject.getAccessCount() == thatCachedObject.getAccessCount() ?
                (thisCachedObject.getCreateTime() < thatCachedObject.getCreateTime() ? -1
                        : (thisCachedObject.getCreateTime() == thatCachedObject.getCreateTime() ? 0 : 1))
                : 1;
    }

    public static void main(String[] args) {

    }
}
