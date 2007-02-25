package net.sourceforge.jfox.cache;

/**
 * //TODO: 阻塞式 Cache，在一个CachedObject 被使用时，
 * 其它线程等待，直到对方使用完毕才继续执行
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class BlockingCache extends SimpleCache {

    public BlockingCache(CacheConfig config) {
        super(config);
    }

    public static void main(String[] args) {

    }
}
