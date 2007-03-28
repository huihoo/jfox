package org.jfox.entity.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 封装一个Cache Object
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class CachedObject implements Serializable {
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

    private long createTime = System.currentTimeMillis();

    private long lastAccessTime;

    private long accessCount;

    private boolean inUse = false;

    public CachedObject(Serializable key, Serializable object) {
        this.key = key;
        this.instance = object;

        try {
            size = sizeOf(object);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Object not serializable! " + object);
        }

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

    void reset() {
        instance = null;
        size = 0;
        createTime = System.currentTimeMillis();
        accessCount = 1;
        lastAccessTime = createTime;
    }

    void updateStatistics() {
        accessCount++;
        lastAccessTime = System.currentTimeMillis();
    }

    private static long sizeOf(final Object obj) throws IOException {
        assert obj != null;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(4096);
        ObjectOutputStream out = new ObjectOutputStream(buf);
        out.writeObject(obj);
        out.flush();
        buf.close();
        return buf.size();
    }

    public static void main(String[] args) {

    }
}
