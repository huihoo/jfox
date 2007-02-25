package net.sourceforge.jfox.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SimpleCache implements Cache {

    private CacheConfig config;

    private Storage storage = new Storage();

    public SimpleCache(CacheConfig config) {
        this.config = config;
    }

    public CacheConfig getConfig() {
        return config;
    }

    public CacheStatus getStatus() {
        return null;
    }

    public void store(Serializable key, Serializable obj) {
    }

    public Serializable retrieve(Serializable key) {
        return null;
    }

    public Serializable remove(Serializable key) {
        return null;
    }

    public boolean contains(Serializable key) {
        return false;
    }

    public void clean() {
    }

    public void clear() {
    }

    //TODO: 用来存储所有的cache 对象
    class Storage {
        /**
         * 用来索引
         */
        private Map<Serializable, CachedObject> cacheMap = new HashMap<Serializable, CachedObject>();
        /**
         * 用来排序
         */
        private List<CachedObject> cacheList = new ArrayList<CachedObject>();
        
    }

    public static void main(String[] args) {

    }
}
