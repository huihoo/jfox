package net.sourceforge.jfox.cache;

import java.util.Comparator;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class LFUComparator implements Comparator<CachedObject> {

    //TODO: 考虑 inUse
    public int compare(CachedObject co1, CachedObject co2) {
        return co1.getAccessCount() < co2.getAccessCount() ? -1
                : co1.getAccessCount() == co2.getAccessCount() ?
                (co1.getCreateTime() < co2.getCreateTime() ? -1
                        : (co1.getCreateTime() == co2.getCreateTime() ? 0 : 1))
                : 1;
    }

    public static void main(String[] args) {

    }
}
