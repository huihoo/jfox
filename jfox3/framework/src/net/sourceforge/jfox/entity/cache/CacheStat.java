package net.sourceforge.jfox.entity.cache;

/**
 * 描述Cache当前的状态信息
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class CacheStat {

    private long size = 0;
    private long hits = 0;
    private long misses = 0;
    private long puts = 0;
    private long removes = 0;
    private long memorySize = 0;

    public void increaseHits() {
        hits++;
    }

    public void increaseMisses() {
        misses++;
    }

    public void increasePuts() {
        puts++;
    }

    public void increaseRemoves() {
        removes++;
    }

    public void increaseSize() {
        size++;
    }

    public void decreaseSize() {
        size--;
    }

    public void increaseMemorySize(long memorySize) {
        this.memorySize += memorySize;
    }

    public void decreaseMemorySize(long memorySize) {
        this.memorySize -= memorySize;
    }

    public long getHits() {
        return hits;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public long getMisses() {
        return misses;
    }

    public long getSize() {
        return size;
    }

    public long getPuts() {
        return puts;
    }

    public long getRemoves() {
        return removes;
    }

    public synchronized void reset() {
        hits = 0;
        misses = 0;
        puts = 0;
        removes = 0;
    }

    public String toString() {
        return "hit:" + getHits() + " miss:" + getMisses() + " memorySize:" + getMemorySize();
        //return "hit:"+_hit+" miss:"+_miss+" memorySize:"+_memorySize+" size:"+_map.size()+" tsize:"+_tmap.size();
    }
}
