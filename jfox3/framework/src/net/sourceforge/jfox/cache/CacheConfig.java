package net.sourceforge.jfox.cache;

import java.util.Comparator;
import java.io.Serializable;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class CacheConfig {

    /**
     * 支持的 Evicition 算法
     */
    public static enum Algorithm {
        LRU, LFU, FIFO
    }

    /**
     * cache id，一般以cache对象的类名作为id
     */
    private String cacheId;

    /**
     * 最大生存时间 <=0 表示无限
     */
    private long ttl = 0;

    /**
     * 对象的最大的休眠事件，<=0表示无限
     */
    private long idleTime = 0;

    /**
     * 能使用的最大内存数, 默认 10M
     */
    private long maxMemorySize = 10 * 1024 * 1024;

    /**
     * 最大缓存的对象数目，默认 1000
     */
    private int maxSize = 1000;

    /**
     * 使用何种类型的Cache，Simple/Synchronized/Blocking
     */
    private String type;

    /**
     * 采用的Eviction算法
     */
    private Algorithm algorithm;

    private Cache instance = null;

    public CacheConfig(String cacheId, String type, Algorithm algorithm, int maxSize, long maxMemorySize, long idleTime, long ttl) {
        this.cacheId = cacheId;
        this.type = type;
        this.algorithm = algorithm;
        this.maxSize = maxSize;
        this.maxMemorySize = maxMemorySize;
        this.idleTime = idleTime;
        this.ttl = ttl;
    }


    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public Comparator<CachedObject> getAlgorithmComparator() {
        switch (getAlgorithm()) {
            case LFU:
                return new LFUComparator();
            case LRU:
                return new LRUComparator();
            default:
                return new LRUComparator();
        }
    }

    public String getCacheId() {
        return cacheId;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public long getMaxMemorySize() {
        return maxMemorySize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public long getTtl() {
        return ttl;
    }

    public String getType() {
        return type;
    }

    /**
     * 根据 Config 创建 Cache，如果已经创建，则返回创建的
     */
    public synchronized Cache getCache(){
        if(instance == null) {
            instance = new SimpleCache(this);
        }
        return instance;
    }

    /**
     * 包装需要 cached 的 对象成 CachedObject
     */
    public CachedObject newCachedObject(Serializable key, Serializable value){
        return null;
    }

    public static void main(String[] args) {

    }
}
