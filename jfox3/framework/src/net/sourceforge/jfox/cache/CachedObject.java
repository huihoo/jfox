package net.sourceforge.jfox.cache;

import java.io.Serializable;

/**
 * 封装一个Cache Object
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class CachedObject implements Serializable{
    /**
     * cache 的 key
     */
    private Serializable key = null;

    /**
     * cache 的实例
     */
    private Serializable instance = null;

    private long size = -1;

    private long expireTime = -1;

    private long createTime;

    private long lastAccessTime;

    private long accessCount;

    private boolean inUse = false;

    public CachedObject(Serializable _key, Serializable _object) {
        this.key = _key;
        this.instance = _object;
    }

    public Serializable getKey() {
        return key;
    }

    public Serializable getObject() {
        return this.instance;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public long getSize() {
        return size;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public static void main(String[] args) {

    }
}
