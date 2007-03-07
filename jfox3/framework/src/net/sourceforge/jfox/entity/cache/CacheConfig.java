package net.sourceforge.jfox.entity.cache;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class CacheConfig {

    private ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    /**
     * 支持的 Evicition 算法
     */
    public static enum Algorithm {
        LRU, LFU, FIFO
    }

    /**
     * 最大生存时间 <=0 表示无限
     */
    private long ttl = 0;

    /**
     * 对象的最大的休眠时间，<=0表示无限
     */
    private long maxIdleTime = 0;

    /**
     * 能使用的最大内存数, 默认 10M
     */
    private long maxMemorySize = 10 * 1024 * 1024;

    /**
     * 最大缓存的对象数目，默认 1000
     */
    private int maxSize = 1000;

    /**
     * 采用的Eviction算法
     */
    private Algorithm algorithm = Algorithm.LRU;

    private Map<String, Cache> caches = new HashMap<String, Cache>();

    public CacheConfig(Algorithm algorithm, int maxSize, long maxMemorySize, long maxIdleTime, long ttl) {
        this.algorithm = algorithm;
        this.maxSize = maxSize;
        this.maxMemorySize = maxMemorySize;
        this.maxIdleTime = maxIdleTime;
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

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public long getMaxMemorySize() {
        return maxMemorySize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public long getTTL() {
        return ttl;
    }

    /**
     * 根据 Config 创建 Cache，如果已经创建，则返回创建的
     *
     * @param cacheName cache name
     */
    public synchronized Cache buildCache(String cacheName) {
        if(caches.isEmpty()) {
            cleaner.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    for(Cache cache : caches.values()) {
                        cache.clean();
                    }
                }
            }, getMaxIdleTime() / 10, getMaxIdleTime() / 2 , TimeUnit.MILLISECONDS);
        }
        if (!caches.containsKey(cacheName)) {
            Cache cache = new SimpleCache(this);
            caches.put(cacheName, cache);
        }
        return caches.get(cacheName);
    }

    public void close() {
        cleaner.shutdown();
    }

    public static void main(String[] args) {
        CacheConfig cacheConfig = new CacheConfig(CacheConfig.Algorithm.LRU, 2, 10 * 1024, 60 * 1000, 0);
        Cache cache = cacheConfig.buildCache("DEFAULT");
        cache.put("a", "1");
        cache.put("b", "2");
        System.out.println(cache.get("a"));
        System.out.println(cache.get("b"));
        cache.remove("b");
        System.out.println(cache.get("b"));
        System.out.println(cache.getStatus().getSize());
        System.out.println(cache.getStatus().getPuts());
        System.out.println(cache.getStatus().getRemoves());
        System.out.println(cache.getStatus().getSize());

        System.out.println("shutdown...");
        cacheConfig.close();
    }
}
