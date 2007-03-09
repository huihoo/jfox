package net.sourceforge.jfox.entity.cache;

import java.io.Serializable;

/**
 * //TODO: 只缓存一个对象实例，以实现Singleton
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class SingletonCache implements Cache {

    private CacheConfig config;

    private Storage storage = new Storage();

    public SingletonCache(CacheConfig config) {
        this.config = config;
    }

    public CacheConfig getConfig() {
        return config;
    }

    public CacheStat getStatus() {
        return null;
    }

    public void put(Serializable key, Serializable obj) {
    }

    public Serializable get(Serializable key) {
        return storage.getCachedObject();
    }

    public Serializable remove(Serializable key) {
        CachedObject cachedObject = storage.getCachedObject();
        storage.setCachedObject(null);
        return cachedObject.getObject();
    }

    public boolean contains(Serializable key) {
        return false;
    }

    public void clean() {
    }

    public void clear() {
    }

    class Storage {
        private CachedObject cachedObject;

        public CachedObject getCachedObject() {
            return cachedObject;
        }

        public void setCachedObject(CachedObject cachedObject) {
            this.cachedObject = cachedObject;
        }
    }

    public static void main(String[] args) {

    }
}
