/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SimpleCache implements Cache {

    private CacheConfig config;

    private CacheStat cacheStat = null;

    private String name;

    /**
     * 用来索引
     */
    private Map<Serializable, CachedObject> cacheMap = new ConcurrentHashMap<Serializable, CachedObject>();

    /**
     * 用来排序
     */
    private List<CachedObject> cacheList = new ArrayList<CachedObject>();

    SimpleCache(String cacheName, CacheConfig config) {
        this.name = cacheName;
        this.config = config;
        this.cacheStat = new CacheStat();
    }

    public String getName() {
        return name;
    }

    public CacheConfig getConfig() {
        return config;
    }

    public CacheStat getStatus() {
        return cacheStat;
    }

    public Collection<Serializable> keys() {
        return Collections.unmodifiableCollection(cacheMap.keySet());
    }

    public synchronized void put(Serializable key, Serializable obj) {
        assert key != null;
        assert obj != null;

        if (cacheMap.containsKey(key)) {
            CachedObject cachedObject = cacheMap.get(key);
            removeCachedObject(cachedObject);
        }
        CachedObject cachedObject = new CachedObject(key, obj);
        long size = cachedObject.getSize();
        checkOverflow(size);

        cacheStat.increasePuts();
        cacheStat.increaseSize();
        cacheStat.increaseMemorySize(size);
        cacheMap.put(key, cachedObject);

        cacheList.add(cachedObject);
        Collections.sort(cacheList, config.getAlgorithmComparator());
    }

    private void checkOverflow(long objSize) {
        while ((config.getMaxSize() > 0 && cacheMap.size() + 1 > config.getMaxSize())
                || (config.getMaxMemorySize() > 0
                && cacheStat.getMemorySize() + objSize > config.getMaxMemorySize())) {

            if (!cacheList.isEmpty()) {
                CachedObject cachedObject = cacheList.get(0);
                removeCachedObject(cachedObject);
            }
        }
    }

    public Serializable get(Serializable key) {
        assert key != null;

        CachedObject cachedObject = cacheMap.get(key);
        if (cachedObject != null) {
            Serializable obj = cachedObject.getObject();
            if (obj != null) {
                if (validCachedObject(cachedObject)) {
                    cachedObject.updateStatistics();
                    cacheStat.increaseHits();
                    return obj;
                }
                else {
                    remove(key);
                    cacheStat.increaseMisses();
                    return null;
                }
            }
        }
        else {
            cacheStat.increaseMisses();
        }
        return null;
    }

    public synchronized Serializable remove(Serializable key) {
        assert key != null;
        cacheStat.increaseRemoves();
        if (cacheMap.containsKey(key)) {
            CachedObject cachedObject = cacheMap.get(key);
            removeCachedObject(cachedObject);
        }
        return null;
    }

    public boolean contains(Serializable key) {
        return cacheMap.containsKey(key);
    }

    public synchronized void clean() {
        if (config.getTTL() == 0 && config.getMaxIdleTime() == 0) {
            return;
        }

        for (CachedObject co : cacheList) {
            if (!validCachedObject(co)) {
                remove(co.getKey());
            }
        }
    }

    public synchronized void clear() {
        cacheList.clear();
        cacheMap.clear();
        cacheStat.reset();
    }

    protected boolean validCachedObject(CachedObject cachedObject) {
        long curTime = System.currentTimeMillis();
        return (config.getTTL() == 0 || (cachedObject.getCreateTime() + config.getTTL()) >= curTime)
                &&
                cachedObject.getObject() != null;

    }

    protected void removeCachedObject(CachedObject cachedObject) {
        cacheList.remove(cachedObject);
        cacheStat.decreaseSize();
        long memorySize = cachedObject.getSize();
        cacheStat.decreaseMemorySize(memorySize);
        cacheMap.remove(cachedObject.getKey());
        cachedObject.reset();
    }

    public String toString() {
        return "SimpleCache@"+getName()+"#"+getConfig().toString() ;
    }

    public static void main(String[] args) {

    }
}
