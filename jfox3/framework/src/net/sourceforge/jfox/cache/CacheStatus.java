package net.sourceforge.jfox.cache;

/**
 * 描述Cache当前的状态信息
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public interface CacheStatus {

    public long getSize();
   
    public long getCacheHits();

    public long getCacheMisses();

    public long getTotalPuts();

    public long getTotalRemoves();

    public long getCacheUses();

    public long getMemorySize();
}
