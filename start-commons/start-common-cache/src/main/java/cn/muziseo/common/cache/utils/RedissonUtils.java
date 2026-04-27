package cn.muziseo.common.cache.utils;

import cn.muziseo.common.core.utils.spring.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;

import java.util.Collection;

/**
 * Redisson 分布式锁工具类
 *
 * @author 木子软件
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedissonUtils {

    private static RedissonClient client() {
        return SpringUtils.getBean(RedissonClient.class);
    }

    /**
     * 获取客户端实例
     */
    public static RedissonClient getClient() {
        return client();
    }

    /**
     * 获取可重入锁
     *
     * @param lockKey 锁键
     */
    public static RLock getLock(String lockKey) {
        return client().getLock(lockKey);
    }

    /**
     * 获取公平锁
     *
     * @param lockKey 锁键
     */
    public static RLock getFairLock(String lockKey) {
        return client().getFairLock(lockKey);
    }

    /**
     * 获取读写锁
     *
     * @param lockKey 锁键
     */
    public static RReadWriteLock getReadWriteLock(String lockKey) {
        return client().getReadWriteLock(lockKey);
    }

    /**
     * 获取读锁
     *
     * @param lockKey 锁键
     */
    public static RLock getReadLock(String lockKey) {
        return client().getReadWriteLock(lockKey).readLock();
    }

    /**
     * 获取写锁
     *
     * @param lockKey 锁键
     */
    public static RLock getWriteLock(String lockKey) {
        return client().getReadWriteLock(lockKey).writeLock();
    }

    /**
     * 获取信号量
     *
     * @param lockKey 锁键
     */
    public static RSemaphore getSemaphore(String lockKey) {
        return client().getSemaphore(lockKey);
    }

    /**
     * 获取闭锁
     *
     * @param lockKey 锁键
     */
    public static RCountDownLatch getCountDownLatch(String lockKey) {
        return client().getCountDownLatch(lockKey);
    }

    /**
     * 删除对象
     *
     * @param key 键
     */
    public static boolean deleteObject(String key) {
        return client().getBucket(key).delete();
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     */
    public static void deleteObject(Collection<?> collection) {
        RBatch batch = client().createBatch();
        collection.forEach(t -> batch.getBucket(t.toString()).deleteAsync());
        batch.execute();
    }

}
